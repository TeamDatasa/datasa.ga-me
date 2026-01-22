package datasa.service;

import domain.dto.*;
import datasa.repository.TripRepository;
import datasa.repository.UserRepository;
import domain.entity.Trip;
import domain.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripService {
	private final TripRepository tripRepository;
	private final UserRepository userRepository;
	
	public TripDetailResponse getTripDetail(Long boardNum) {
		Trip entity = tripRepository.findById(boardNum).orElseThrow(() -> new EntityNotFoundException("해당 번호의 글 없습니다"));
		
		return TripDetailResponse.builder()
				.tripId(entity.getTripId())
				.hostUser(entity.getHostUser())
				.title(entity.getTitle())
				.description(entity.getDescription())
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
				.build();

	}
	
	
	 /**
	  * U_001 여행 목록 조회
	  * - latest (기본): 최신순
	  * - popular: 인기순
	  */
	public Page<TripListResponseDto> getTripList(String order, Pageable pageable) {
		if ("popular".equalsIgnoreCase(order)) {
			return tripRepository.findPopularTrips(pageable);
		}
		return tripRepository.findLatestTrips(pageable);
	}
	
	 /**
	  * U_002 여행 검색 (언어 기반)
	  * - latest / popular 지원
	  */
	 public Page<TripListResponseDto> searchByFilters(
			 List<String> languages,
			 String region,
			 String theme,
			 String order,
			 Pageable pageable
	 ) {
		 if ("popular".equalsIgnoreCase(order)) {
			 return tripRepository.searchByFiltersPopular(region, theme, languages, pageable);
		 }
		 return tripRepository.searchByFilters(region, theme, languages, pageable);
	 }
	 // bjh
	 @Transactional
	 public Long write(Long hostUserId, TripWriteRequest request) {
		
	 	User hostUser = userRepository.findById(hostUserId)
	 			.orElseThrow(() -> new IllegalArgumentException("호스트 유저가 존재하지 않습니다. id=" + hostUserId));
		
	 	Trip trip = new Trip();
	 	trip.setHostUser(hostUser);
	 	trip.setTitle(request.getTitle());
	 	trip.setDescription(request.getDescription());
	 	trip.setEstimatedCost(request.getEstimatedCost());
	 	trip.setMaxParticipants(request.getMaxParticipants());
	 	trip.setDurationMinutes(request.getDurationMinutes());
	 	trip.setStartAt(request.getStartAt());
	 	trip.setEndAt(request.getEndAt());
	 	trip.setStatus(Trip.Status.OPEN); // 임의
	 	trip.setTheme(request.getTheme());
		
	 	Trip saved = tripRepository.save(trip);
	 	return saved.getTripId();
	 }
	
	 // bjh
	 public List<TripListResponse> getListAll() {
	 	Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		
	 	List<Trip> entityList = tripRepository.findAll(sort);
	 	List<TripListResponse> dtoList = new ArrayList<>();
	 	for (Trip entity : entityList) {
	 		TripListResponse dto = TripListResponse.builder()
	 				.tripId(entity.getTripId())
	 				.hostUserId(entity.getHostUser().getUserId())
	 				.title(entity.getTitle())
	 				.description(entity.getDescription())
	 				.estimatedCost(entity.getEstimatedCost())
	 				.maxParticipants(entity.getMaxParticipants())
	 				.durationMinutes(entity.getDurationMinutes())
	 				.startAt(entity.getStartAt())
	 				.endAt(entity.getEndAt())
	 				.status(entity.getStatus())
	 				.createdAt(entity.getCreatedAt())
	 				.updatedAt(entity.getUpdatedAt())
	 				.build();
	 		dtoList.add(dto);
	 	}
	 	return dtoList;
	 }
	
	 @Transactional
	 public void updateTrip(TripUpdateRequest req, Long loginUserId) {
		
	 	Trip trip = tripRepository.findById(req.getTripId())
	 			.orElseThrow(() -> new EntityNotFoundException("게시글 없습니다."));
		
	 	// 작성자 검증
	 	if (!trip.getHostUser().getUserId().equals(loginUserId)) {
	 		throw new RuntimeException("수정 권한이 없습니다.");
	 	}
		
	 	// 전체 수정
	 	trip.setTitle(req.getTitle());
	 	trip.setDescription(req.getDescription());
	 	trip.setEstimatedCost(req.getEstimatedCost());
	 	trip.setMaxParticipants(req.getMaxParticipants());
	 	trip.setDurationMinutes(req.getDurationMinutes());
	 	trip.setStartAt(req.getStartAt());
	 	trip.setEndAt(req.getEndAt());
	 	trip.setTheme(req.getTheme());
	 }
	
	
	 @Transactional
	 public void deleteTrip(Long tripId, Long loginUserId) {
	 	Trip trip = tripRepository.findById(tripId)
	 			.orElseThrow(() -> new EntityNotFoundException("게시글 없습니다. id=" + tripId));
		
	 	Long hostId = trip.getHostUser().getUserId();
	 	if (!hostId.equals(loginUserId)) {
	 		throw new RuntimeException("삭제 권한이 없습니다.");
	 	}
		
	 	tripRepository.delete(trip);
	 }
	

    /**
     * U_001 여행 목록 조회
     * - latest (기본): 최신순
     * - popular: 인기순
     */
//    public Page<TripListResponseDto> getTripList(
//            String order,
//            Pageable pageable
//    ) {
//        if ("popular".equalsIgnoreCase(order)) {
//            return tripRepository.findPopularTrips(pageable);
//        }
//        return tripRepository.findLatestTrips(pageable);
//    }

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
        if ("popular".equalsIgnoreCase(order)) {
            return tripRepository.searchByFiltersPopular(
                    region, theme, languages, pageable
            );
        }

        return tripRepository.searchByFilters(
                region, theme, languages, pageable
        );
    }

    /**
     * U_003 여행 상세 조회
     */
//    public TripDetailResponseDto getTripDetail(Long tripId) {
//        return tripRepository.findTripDetail(tripId)
//                .orElseThrow(() ->
//                        new IllegalArgumentException("존재하지 않는 여행입니다.")
//                );
//    }
}
