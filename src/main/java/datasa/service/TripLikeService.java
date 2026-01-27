package datasa.service;

import datasa.domain.dto.TripListResponse;
import datasa.domain.entity.Trip;
import datasa.domain.entity.TripLike;
import datasa.domain.entity.User;
import datasa.repository.TripLikeRepository;
import datasa.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TripLikeService {
	
	private final TripRepository tripRepository;
	private final TripLikeRepository tripLikeRepository;
	
	public TripLikeResult toggleLike(Long tripId, User user) {
		Trip trip = tripRepository.findById(tripId)
				.orElseThrow(() -> new IllegalArgumentException("Trip 없음"));
		
		
		boolean nowLiked;
		
		
		if (tripLikeRepository.existsByTripAndUser(trip, user)) {
			tripLikeRepository.deleteByTripAndUser(trip, user);
			nowLiked = false;
		} else {
			tripLikeRepository.save(TripLike.of(trip, user));
			nowLiked = true;
		}
		
		
		long count = tripLikeRepository.countByTrip(trip);
		return new TripLikeResult(nowLiked, count);
	}
	
	
	public record TripLikeResult(boolean liked, long count) {}

	
	public List<TripListResponse> getListAll(Long userId) {
		List<Trip> trips = tripRepository.findAll(); // 기존 로직 유지(정렬 등은 기존대로)
		List<TripListResponse> list = trips.stream()
				.map(TripListResponse::from)
				.toList();
		
		if (list.isEmpty()) return list;
		
		List<Long> tripIds = list.stream().map(TripListResponse::getTripId).toList();
		
		// 1) count 맵 만들기
		var countRows = tripLikeRepository.countByTripIds(tripIds);
		java.util.Map<Long, Long> countMap = new java.util.HashMap<>();
		for (Object[] row : countRows) {
			Long tripId = (Long) row[0];
			Long cnt = (Long) row[1];
			countMap.put(tripId, cnt);
		}
		
		// 2) liked set 만들기
		var likedTripIds = tripLikeRepository.findLikedTripIds(userId, tripIds);
		java.util.Set<Long> likedSet = new java.util.HashSet<>(likedTripIds);
		
		// 3) DTO 다시 빌드(불변 DTO 유지)
		return list.stream()
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
}
