package datasa.service;

import datasa.domain.dto.RatingSummary;
import datasa.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {
	
	private final ReviewRepository reviewRepository;
	
	@Transactional(readOnly = true)
	public RatingSummary getTripRating(Long tripId) {
		long count = reviewRepository.countByTrip_TripId(tripId);
		double avg = (count == 0) ? 0.0 : reviewRepository.avgRatingByTripId(tripId);
		return RatingSummary.of(count, avg);
	}
	
	@Transactional(readOnly = true)
	public RatingSummary getHostRating(Long hostUserId) {
		long count = reviewRepository.countByHostId(hostUserId);
		double avg = (count == 0) ? 0.0 : reviewRepository.avgRatingByHostId(hostUserId);
		return RatingSummary.of(count, avg);
	}
}
