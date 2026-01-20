package datasa.service;

import datasa.entity.Trip;
import datasa.entity.User;
import datasa.repository.TripRepository;
import datasa.repository.UserRepository;
import domain.dto.TripListResponse;
import domain.dto.TripWriteRequest;
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
	
    /**
     * U_001 여행 목록 조회
     * - latest (기본): 최신순
     * - popular: 인기순
     */
    public Page<Trip> getTripList(
            String order,
            Pageable pageable
    ) {
        // 인기순
        if ("popular".equalsIgnoreCase(order)) {
            return tripRepository.findPopularTrips(pageable);
        }

        // 기본: 최신순
        return tripRepository.findByStatus(
                Trip.Status.OPEN,
                pageable
        );
    }

    /**
     * U_002 여행 검색 (언어 기반)
     * - latest / popular 지원
     */
    public Page<Trip> searchByFilters(
            String language,
            String order,
            Pageable pageable
    ) {
        // 인기순 검색
        if ("popular".equalsIgnoreCase(order)) {
            return tripRepository.searchByFiltersPopular(
                    language,
                    pageable
            );
        }

        // 최신순 검색
        return tripRepository.searchByFilters(
                language,
                pageable
        );
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
		trip.setStatus(Trip.Status.OPEN);
		
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
}
