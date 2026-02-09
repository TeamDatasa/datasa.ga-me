package datasa.service;


import datasa.domain.dto.*;
import datasa.domain.entity.*;
import datasa.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
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
	private final TripRepository tripRepository;
	private final UserRepository userRepository;
	private final ApplicationRepository applicationRepository;
	private final TripLanguageRepository tripLanguageRepository;
	private final TripLocationRepository tripLocationRepository;
	private final LocationRepository locationRepository;
	
	private static final Long TEST_USER_ID = 1L;
	private final TripLikeRepository tripLikeRepository;


//	public TripDetailResponse getTripDetail(Long boardNum) {
//		Trip entity = tripRepository.findById(boardNum).orElseThrow(() -> new EntityNotFoundException("해당 번호의 글 없습니다"));
//
//		return TripDetailResponse.builder()
//				.tripId(entity.getTripId())
//				.hostUser(entity.getHostUser())
//				.title(entity.getTitle())
//				.description(entity.getDescription())
//				.estimatedCost(entity.getEstimatedCost())
//				.maxParticipants(entity.getMaxParticipants())
//				.durationMinutes(entity.getDurationMinutes())
//				.startAt(entity.getStartAt())
//				.endAt(entity.getEndAt())
//				.status(entity.getStatus())
//				.theme(entity.getTheme())
//				.createdAt(entity.getCreatedAt())
//				.updatedAt(entity.getUpdatedAt())
//				.editLockDays(entity.getEditLockDays())
//				.build();
//
//	}

//	// bjh
//	@Transactional
//	public Long write(Long hostUserId, TripWriteRequest request) {
//
//		User hostUser = userRepository.findById(hostUserId)
//				.orElseThrow(() -> new IllegalArgumentException("호스트 유저가 존재하지 않습니다. id=" + hostUserId));
//
//		Trip trip = new Trip();
//		trip.setHostUser(hostUser);
//		trip.setTitle(request.getTitle());
//		trip.setDescription(request.getDescription());
//		trip.setRegion(request.getRegion());
//		trip.setEstimatedCost(request.getEstimatedCost());
//		trip.setMaxParticipants(request.getMaxParticipants());
//		trip.setDurationMinutes(request.getDurationMinutes());
//		trip.setStartAt(request.getStartAt());
//		trip.setEndAt(request.getEndAt());
//		trip.setStatus(Trip.Status.OPEN); // 임의
//		trip.setTheme(request.getTheme());
//
//		Trip saved = tripRepository.save(trip);
//		return saved.getTripId();
//	}
	
	// bjh
	@Transactional(readOnly = true)
	public List<TripListResponse> getListAll() {
		
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		
		List<Trip> entityList = tripRepository.findAll(sort);
		if (entityList.isEmpty()) return List.of();
		
		// 1) DTO 기본 구성
		List<TripListResponse> baseList = entityList.stream()
				.map(TripListResponse::from)
				.toList();
		
		List<Long> tripIds = baseList.stream()
				.map(TripListResponse::getTripId)
				.toList();
		
		// 2) 좋아요 카운트 일괄 조회
		java.util.Map<Long, Long> countMap = new java.util.HashMap<>();
		for (Object[] row : tripLikeRepository.countByTripIds(tripIds)) {
			Long tripId = (Long) row[0];
			Long cnt = (Long) row[1];
			countMap.put(tripId, cnt);
		}
		
		// 3) 내가 좋아요한 tripId 일괄 조회
		java.util.Set<Long> likedSet = new java.util.HashSet<>(
				tripLikeRepository.findLikedTripIds(TEST_USER_ID, tripIds)
		);
		
		// 4) enrich 후 반환(불변 DTO 유지)
		return baseList.stream()
				.map(dto -> TripListResponse.builder()
						.tripId(dto.getTripId())
						.hostUserId(dto.getHostUserId())
						.hostName(dto.getHostName())
						.title(dto.getTitle())
						.description(dto.getDescription())
						.estimatedCost(dto.getEstimatedCost())
						.maxParticipants(dto.getMaxParticipants())
						.durationMinutes(dto.getDurationMinutes())
						.startAt(dto.getStartAt())
						.endAt(dto.getEndAt())
						.status(dto.getStatus())
						.createdAt(dto.getCreatedAt())
						.updatedAt(dto.getUpdatedAt())
						.likeCount(countMap.getOrDefault(dto.getTripId(), 0L))
						.likedByMe(likedSet.contains(dto.getTripId()))
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
		
		// ✅ 기존 위치 전부 삭제를 "먼저" DB에 반영(Flush)
		tripLocationRepository.deleteAllByTripId(trip.getTripId());
		
		// ✅ 새로 저장
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
		
		tripRepository.delete(trip);
	}


	
	
	/**
	 * U_001 여행 목록 조회
	 * - latest (기본): 최신순
	 * - popular: 인기순
	 */
	public Page<TripListResponseDto> getTripList(
			String order,
			Pageable pageable
	) {
		if ("popular".equalsIgnoreCase(order)) {
			return tripRepository.findPopularTrips(pageable);
		}
		return tripRepository.findLatestTrips(pageable);
	}
//	@Transactional(readOnly = true)
//	public List<TripListResponseDto> getMyTrips(Long hostUserId) {
//		return tripRepository.findMyTrips(hostUserId);
//	}
	
	
	/**
	 * U_002 여행 검색
	 * - 언어(복수) / 지역 / 테마
	 * - latest / popular 지원
	 */
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
                return tripRepository.searchByFiltersPopular(
                        region, theme, languages, pageable
                );
            } else {
                return tripRepository.searchByFiltersPopularNoLang(
                        region, theme, pageable
                );
            }
        } else { // latest
            if (hasLang) {
                return tripRepository.searchByFilters(
                        region, theme, languages, pageable
                );
            } else {
                return tripRepository.searchByFiltersNoLang(
                        region, theme, pageable
                );
            }
        }
    }

	/**
	 * 여행 상세페이지에서 언어랑 승인인원
	 * 및 신청
	 *
	 */
	public TripDetailResponseDto getTripDetailview(Long tripId) {
		Trip trip = tripRepository.findById(tripId)
				.orElseThrow(() ->
						new EntityNotFoundException("해당 여행이 존재하지 않습니다.")
				);
		
		long approvedCount =
				applicationRepository.countByTrip_TripIdAndStatus(
						tripId,
						Application.Status.APPROVED
				);
		
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
				"actice" // 임시
		);
	}
	
	
	public TripDetailResponse getTripDetail(Long boardNum, Long loginUserId) {
		Trip entity = tripRepository.findById(boardNum)
				.orElseThrow(() -> new EntityNotFoundException("해당 번호의 글 없습니다"));
		
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
				.build();
	}


	
	@Transactional
	public Long write(Long userId, TripWriteRequest request) {
		User hostUser = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("호스트 유저가 존재하지 않습니다. id=" + userId));
		
		Trip trip = new Trip();
		trip.setHostUser(hostUser);
		trip.setTitle(request.getTitle());
		trip.setDescription(request.getDescription());
		trip.setRegion(request.getRegion());
		trip.setEstimatedCost(request.getEstimatedCost());
		trip.setMaxParticipants(request.getMaxParticipants());
		trip.setDurationMinutes(request.getDurationMinutes());
		trip.setStartAt(request.getStartAt());
		trip.setEndAt(request.getEndAt());
		trip.setStatus(Trip.Status.OPEN); // test
		trip.setTheme(request.getTheme());
		Trip saved = tripRepository.save(trip);
		
		saveTripLocations(saved, request);
		return saved.getTripId();
	}
	
	// schedulePlaces 우선 저장, 없으면 단일(placeName) 저장
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
		// 현재는 google_place_id 컬럼에 kakao placeId 저장
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
	
	
	/**
	 * 여행 상세페이지에서 언어랑 승인인원
	 * 및 신청
	 *
	 */
	public TripDetailResponseDto getTripDetail_jiwon(Long tripId, Long userId) {
		
		// 1️⃣ 여행 조회
		Trip trip = tripRepository.findById(tripId)
				.orElseThrow(() ->
						new EntityNotFoundException("해당 여행이 존재하지 않습니다.")
				);
		
		// 2️⃣ 승인 인원 수
		long approvedCount =
				applicationRepository.countByTrip_TripIdAndStatus(
						tripId,
						Application.Status.APPROVED
				);
		
		// 3️⃣ 언어 목록
		List<String> languages =
				tripLanguageRepository.findByTrip_TripId(tripId)
						.stream()
						.map(TripLanguage::getLanguageCode)
						.toList();
		
		// 4️⃣ 내 신청 상태
		String applicationStatus =
				applicationRepository
						.findByTrip_TripIdAndUser_UserId(tripId, userId)
						.map(app -> app.getStatus().name())
						.orElse(null); // 아직 신청 안 함
		
		// 5️⃣ DTO 조립
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



