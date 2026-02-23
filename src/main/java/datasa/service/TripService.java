package datasa.service;

import datasa.domain.dto.*;
import datasa.domain.entity.*;
import datasa.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripService {

	private static final Long TEST_USER_ID = 1L;

	private final TripRepository tripRepository;
	private final UserRepository userRepository;
	private final ApplicationRepository applicationRepository;
	private final TripLanguageRepository tripLanguageRepository;
	private final TripLocationRepository tripLocationRepository;
	private final LocationRepository locationRepository;
	private final TripLikeRepository tripLikeRepository;

	private final ChatRoomRepository chatRoomRepository;
	private final ChatMemberRepository chatMemberRepository;
	private final CommentRepository commentRepository;
	private final ReviewRepository reviewRepository;
	private final TripViewLogRepository tripViewLogRepository;

	@Transactional(readOnly = true)
	public List<TripListResponse> getListAll(Long loginUserId) {
		
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		List<Trip> entityList = tripRepository.findAll(sort);
		if (entityList.isEmpty()) return List.of();
		
		List<TripListResponse> baseList = entityList.stream()
				.map(TripListResponse::from)
				.toList();
		
		List<Long> tripIds = baseList.stream()
				.map(TripListResponse::getTripId)
				.toList();
		
		Map<Long, List<String>> languageMap = new HashMap<>();
		for (Object[] row : tripLanguageRepository.findCodesByTripIds(tripIds)) {
			Long tripId = (Long) row[0];
			String code = (String) row[1];
			languageMap.computeIfAbsent(tripId, k -> new ArrayList<>()).add(code);
		}
		
		Map<Long, Long> countMap = new HashMap<>();
		for (Object[] row : tripLikeRepository.countByTripIds(tripIds)) {
			Long tripId = (Long) row[0];
			Long cnt = (Long) row[1];
			countMap.put(tripId, cnt);
		}
		
		final Set<Long> likedSet =
				(loginUserId == null)
						? Set.of()
						: new HashSet<>(tripLikeRepository.findLikedTripIds(loginUserId, tripIds));
		
		Map<Long, Long> approvedGuestMap = new HashMap<>();
		for (Object[] row : applicationRepository.countApprovedByTripIds(tripIds)) {
			Long tripId = (Long) row[0];
			Long approvedGuestCount = (Long) row[1];
			approvedGuestMap.put(tripId, approvedGuestCount);
		}
		
		return baseList.stream()
				.map(dto -> TripListResponse.builder()
						.tripId(dto.getTripId())
						.hostUserId(dto.getHostUserId())
						.hostName(dto.getHostName())
						.hostDeleted(dto.isHostDeleted())
						.title(dto.getTitle())
						.description(dto.getDescription())
						.estimatedCost(dto.getEstimatedCost())
						.maxParticipants(dto.getMaxParticipants())
						.currentParticipants(approvedGuestMap.getOrDefault(dto.getTripId(), 0L) + 1L)
						.durationMinutes(dto.getDurationMinutes())
						.startAt(dto.getStartAt())
						.endAt(dto.getEndAt())
						.status(dto.getStatus())
						.createdAt(dto.getCreatedAt())
						.updatedAt(dto.getUpdatedAt())
						.likeCount(countMap.getOrDefault(dto.getTripId(), 0L))
						.likedByMe(likedSet.contains(dto.getTripId()))
						.region(dto.getRegion())
						.theme(dto.getTheme())
						.languageCodes(languageMap.getOrDefault(dto.getTripId(), List.of()))
						.build())
				.toList();
	}

	@Transactional
	public void updateTrip(TripUpdateRequest req, Long loginUserId) {

		Trip trip = tripRepository.findById(req.getTripId())
				.orElseThrow(() -> new EntityNotFoundException("게시글 없습니다."));

		if (!trip.getHostUser().getUserId().equals(loginUserId)) {
			throw new RuntimeException("수정 권한이 없습니다.");
		}

		LocalDateTime limitTime = trip.getStartAt().minusDays(7);
		if (LocalDateTime.now().isAfter(limitTime)) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"여정 시작일 7일 전까지만 수정이 가능합니다."
			);
		}

		trip.setTitle(req.getTitle());
		trip.setDescription(req.getDescription());
		trip.setRegion(req.getRegion());
		trip.setEstimatedCost(req.getEstimatedCost());
		trip.setMaxParticipants(req.getMaxParticipants());
		trip.setDurationMinutes(req.getDurationMinutes());
		trip.setStartAt(req.getStartAt());
		trip.setEndAt(req.getEndAt());
		trip.setTheme(req.getTheme());

		tripLocationRepository.deleteAllByTripId(trip.getTripId());

		if (req.getSchedulePlaces() != null) {
			int order = 1;
			for (TripWriteSchedulePlaceRequest p : req.getSchedulePlaces()) {
				if (p == null || p.getPlaceName() == null || p.getPlaceName().isBlank()) continue;
				persistTripLocation(trip, p, order++);
			}
		}
	}

	@Transactional
	public void deleteTrip(Long tripId, String email) {

		Trip trip = tripRepository.findById(tripId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."
				));

		if (!trip.getHostUser().getEmail().equals(email)) {
			throw new ResponseStatusException(
					HttpStatus.FORBIDDEN, "삭제 권한이 없습니다."
			);
		}

		LocalDateTime limitTime = trip.getStartAt().minusDays(7);
		if (LocalDateTime.now().isAfter(limitTime)) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"여정 시작일 7일 전까지만 삭제가 가능합니다."
			);
		}

		// 1) 채팅방/멤버 선삭제
		chatRoomRepository.findByTrip_TripId(tripId).ifPresent(room -> {
			chatMemberRepository.deleteByChatRoom_RoomId(room.getRoomId());
			chatRoomRepository.delete(room);
		});

		// 2) 좋아요
		tripLikeRepository.deleteAllByTripId(tripId);

		// 3) 위치/언어
		tripLocationRepository.deleteAllByTripId(tripId);
		tripLanguageRepository.deleteByTripId(tripId);

		// 4) 신청/댓글/리뷰/조회로그
		applicationRepository.deleteByTrip_TripId(tripId);
		commentRepository.deleteByTripId(tripId);
		reviewRepository.deleteByTripId(tripId);
		tripViewLogRepository.deleteByTripId(tripId);

		// 5) 마지막에 trip 삭제
		tripRepository.delete(trip);
	}

	public Page<TripListResponseDto> getTripList(String order, Pageable pageable) {
		if ("popular".equalsIgnoreCase(order)) {
			return tripRepository.findPopularTrips(pageable);
		}
		return tripRepository.findLatestTrips(pageable);
	}

	public Page<TripListResponseDto> searchTrips(
			List<String> languages,
			String region,
			String theme,
			String order,
			Pageable pageable
	) {
		boolean hasLang = (languages != null && !languages.isEmpty());

		if ("popular".equals(order)) {
			if (hasLang) {
				return tripRepository.searchByFiltersPopular(region, theme, languages, pageable);
			} else {
				return tripRepository.searchByFiltersPopularNoLang(region, theme, pageable);
			}
		} else {
			if (hasLang) {
				return tripRepository.searchByFilters(region, theme, languages, pageable);
			} else {
				return tripRepository.searchByFiltersNoLang(region, theme, pageable);
			}
		}
	}

	public TripDetailResponseDto getTripDetailview(Long tripId) {
		Trip trip = tripRepository.findById(tripId)
				.orElseThrow(() -> new EntityNotFoundException("해당 여행이 존재하지 않습니다."));

		long approvedCount =
				applicationRepository.countByTrip_TripIdAndStatus(tripId, Application.Status.APPROVED);

		List<String> languages =
				tripLanguageRepository.findByTrip_TripId(tripId)
						.stream()
						.map(TripLanguage::getLanguageCode)
						.toList();

		return new TripDetailResponseDto(
				trip.getTripId(),
				trip.getTitle(),
				trip.getDescription(),
				trip.getRegion(),
				trip.getTheme(),
				trip.getMaxParticipants(),
				trip.getEstimatedCost(),
				trip.getStartAt(),
				trip.getEndAt(),
				trip.getHostUser().getName(),
				approvedCount,
				languages,
				"actice"
		);
	}

	public TripDetailResponse getTripDetail(Long boardNum, Long loginUserId) {
		Trip entity = tripRepository.findById(boardNum)
				.orElseThrow(() -> new EntityNotFoundException("해당 번호의 글 없습니다"));

		List<String> languageCodes = tripLanguageRepository.findByTrip(entity)
				.stream()
				.map(TripLanguage::getLanguageCode)
				.toList();

		List<TripLocationItemResponse> locations = tripLocationRepository
				.findByTripOrderByOrderNoAsc(entity)
				.stream()
				.map(tl -> {
					Location loc = tl.getLocation();
					return TripLocationItemResponse.builder()
							.orderNo(tl.getOrderNo())
							.placeName(loc.getPlaceName())
							.address(loc.getAddress())
							.lat(loc.getLat() != null ? loc.getLat().doubleValue() : null)
							.lng(loc.getLng() != null ? loc.getLng().doubleValue() : null)
							.placeId(loc.getGooglePlaceId())
							.build();
				})
				.toList();

		long likeCount = tripLikeRepository.countByTrip(entity);

		boolean likedByMe = false;
		if (loginUserId != null) {
			User u = new User();
			u.setUserId(loginUserId);
			likedByMe = tripLikeRepository.existsByTripAndUser(entity, u);
		}

		boolean hostDeleted = entity.getHostUser() != null
				&& entity.getHostUser().getStatus() == User.Status.DELETED;

		long approvedGuestCount =
				applicationRepository.countByTrip_TripIdAndStatus(entity.getTripId(), Application.Status.APPROVED);

		long currentParticipants = approvedGuestCount + 1;

		return TripDetailResponse.builder()
				.tripId(entity.getTripId())
				.hostUser(entity.getHostUser())
				.title(entity.getTitle())
				.description(entity.getDescription())
				.region(entity.getRegion())
				.estimatedCost(entity.getEstimatedCost())
				.maxParticipants(entity.getMaxParticipants())
				.durationMinutes(entity.getDurationMinutes())
				.startAt(entity.getStartAt())
				.endAt(entity.getEndAt())
				.status(entity.getStatus())
				.theme(entity.getTheme())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.editLockDays(entity.getEditLockDays())
				.locations(locations)
				.likeCount(likeCount)
				.likedByMe(likedByMe)
				.hostUserId(entity.getHostUser().getUserId())
				.hostName(entity.getHostUser().getName())
				.hostDeleted(hostDeleted)
				.languageCodes(languageCodes)
				.currentParticipants(currentParticipants)
				.build();
	}

	@Transactional
	public Long write(Long userId, TripWriteRequest request) {
		validateWriteRequestTimes(request);

		User hostUser = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("호스트 유저가 존재하지 않습니다. id=" + userId));

		Trip trip = new Trip();
		trip.setHostUser(hostUser);
		trip.setTitle(request.getTitle());
		trip.setDescription(request.getDescription());
		trip.setRegion(request.getRegion());
		trip.setEstimatedCost(request.getEstimatedCost());
		trip.setMaxParticipants(request.getMaxParticipants());
		trip.setStartAt(request.getStartAt());
		trip.setEndAt(request.getEndAt());
		trip.setDurationMinutes(calcDurationMinutes(request.getStartAt(), request.getEndAt()));
		trip.setStatus(Trip.Status.OPEN);
		trip.setTheme(request.getTheme());
		Trip saved = tripRepository.save(trip);

		saveTripLocations(saved, request);
		saveTripLanguages(saved, request);

		return saved.getTripId();
	}

	private void validateWriteRequestTimes(TripWriteRequest request) {
		if (request == null) throw new IllegalArgumentException("잘못된 요청입니다.");
		if (request.getStartAt() == null) throw new IllegalArgumentException("시작 일자/시간은 필수입니다.");
		if (request.getEndAt() == null) throw new IllegalArgumentException("끝나는 일자/시간은 필수입니다.");

		LocalDateTime nowPlus24h = LocalDateTime.now().plusHours(24);
		if (request.getStartAt().isBefore(nowPlus24h)) {
			throw new IllegalArgumentException("게시글을 작성하는 시간으로부터 24시간 이후 일자만 선택 가능합니다");
		}

		if (request.getEndAt().isBefore(request.getStartAt())) {
			throw new IllegalArgumentException("끝나는 일자는 시작 일자 이전의 날을 선택할 수 없습니다");
		}

		LocalDateTime minEndAt = request.getStartAt().plusMinutes(30);
		if (request.getEndAt().isBefore(minEndAt)) {
			throw new IllegalArgumentException("끝나는 일자는 시작 일자/시간으로부터 30분 이후부터 선택 가능합니다");
		}
	}

	private int calcDurationMinutes(LocalDateTime startAt, LocalDateTime endAt) {
		long minutes = java.time.Duration.between(startAt, endAt).toMinutes();
		if (minutes <= 0) {
			throw new IllegalArgumentException("끝나는 일자는 시작 일자 이전의 날을 선택할 수 없습니다");
		}
		if (minutes < 30) {
			throw new IllegalArgumentException("끝나는 일자는 시작 일자/시간으로부터 30분 이후부터 선택 가능합니다");
		}
		if (minutes > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("소요 시간이 너무 깁니다.");
		}
		return (int) minutes;
	}

	private void saveTripLanguages(Trip trip, TripWriteRequest request) {
		List<String> codes = request.getLanguageCodes();
		if (codes == null || codes.isEmpty()) {
			throw new IllegalArgumentException("진행 언어를 1개 이상 선택해 주세요.");
		}

		java.util.LinkedHashSet<String> unique = new java.util.LinkedHashSet<>();
		for (String c : codes) {
			if (c == null) continue;
			String v = c.trim();
			if (!v.isEmpty()) unique.add(v);
		}

		for (String code : unique) {
			TripLanguage tl = new TripLanguage(trip, code);
			tripLanguageRepository.save(tl);
		}
	}

	private void saveTripLocations(Trip trip, TripWriteRequest request) {
		List<TripWriteSchedulePlaceRequest> places = request.getSchedulePlaces();
		if (places != null && !places.isEmpty()) {
			int order = 1;
			for (TripWriteSchedulePlaceRequest p : places) {
				if (p == null || p.getPlaceName() == null || p.getPlaceName().isBlank()) continue;
				persistTripLocation(trip, p, order++);
			}
			return;
		}

		if (request.getPlaceName() != null && !request.getPlaceName().isBlank()) {
			TripWriteSchedulePlaceRequest single = TripWriteSchedulePlaceRequest.builder()
					.placeId(null)
					.placeName(request.getPlaceName())
					.address(request.getAddress())
					.lat(request.getLat())
					.lng(request.getLng())
					.build();
			persistTripLocation(trip, single, 1);
		}
	}

	private void persistTripLocation(Trip trip, TripWriteSchedulePlaceRequest p, int orderNo) {
		String placeId = (p.getPlaceId() == null || p.getPlaceId().isBlank()) ? null : p.getPlaceId();

		Location location = null;
		if (placeId != null) {
			location = locationRepository.findByGooglePlaceId(placeId).orElse(null);
		}

		if (location == null) {
			location = new Location();
			location.setPlaceName(p.getPlaceName());
			location.setAddress(p.getAddress() != null ? p.getAddress() : "");
			location.setLat(p.getLat() != null ? java.math.BigDecimal.valueOf(p.getLat()) : null);
			location.setLng(p.getLng() != null ? java.math.BigDecimal.valueOf(p.getLng()) : null);
			location.setGooglePlaceId(placeId);
			location = locationRepository.save(location);
		}

		TripLocation tl = new TripLocation();
		tl.setTrip(trip);
		tl.setLocation(location);
		tl.setOrderNo(orderNo);
		tripLocationRepository.save(tl);
	}

	public TripDetailResponseDto getTripDetail_jiwon(Long tripId, Long userId) {

		Trip trip = tripRepository.findById(tripId)
				.orElseThrow(() -> new EntityNotFoundException("해당 여행이 존재하지 않습니다."));

		long approvedCount =
				applicationRepository.countByTrip_TripIdAndStatus(tripId, Application.Status.APPROVED);

		List<String> languages =
				tripLanguageRepository.findByTrip_TripId(tripId)
						.stream()
						.map(TripLanguage::getLanguageCode)
						.toList();

		String applicationStatus =
				applicationRepository
						.findByTrip_TripIdAndUser_UserId(tripId, userId)
						.map(app -> app.getStatus().name())
						.orElse(null);

		return new TripDetailResponseDto(
				trip.getTripId(),
				trip.getTitle(),
				trip.getDescription(),
				trip.getRegion(),
				trip.getTheme(),
				trip.getMaxParticipants(),
				trip.getEstimatedCost(),
				trip.getStartAt(),
				trip.getEndAt(),
				trip.getHostUser().getName(),
				approvedCount,
				languages,
				applicationStatus
		);
	}

	public Long findUserIdByEmail(String email) {
		return userRepository.findByEmail(email)
				.map(User::getUserId)
				.orElse(null);
	}

	@Transactional(readOnly = true)
	public List<Trip> getTripsByHost(Long hostUserId) {
		return tripRepository.findByHostUser_UserIdOrderByCreatedAtDesc(hostUserId);
	}
}
