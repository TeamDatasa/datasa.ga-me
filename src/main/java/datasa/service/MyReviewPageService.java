package datasa.service;

import datasa.domain.dto.MyReviewTripItem;
import datasa.domain.entity.Application;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.ApplicationRepository;
import datasa.repository.ReviewRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyReviewPageService {
	
	private final UserRepository userRepository;
	private final ApplicationRepository applicationRepository;
	private final ReviewRepository reviewRepository;
	
	@Transactional(readOnly = true)
	public List<MyReviewTripItem> getMyJoinedTrips(String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		var apps = applicationRepository
				.findByUser_UserIdAndStatusOrderByApplicationIdDesc(
						user.getUserId(),
						Application.Status.APPROVED
				);
		
		var now = LocalDateTime.now();
		
		return apps.stream().map(app -> {
			Trip trip = app.getTrip();
			
			boolean ended = trip.getEndAt().isBefore(now);
			
			var reviewOpt = reviewRepository.findByUser_UserIdAndTrip_TripId(
					user.getUserId(), trip.getTripId()
			);
			
			boolean hasReview = reviewOpt.isPresent();
			boolean canWrite = ended && !hasReview;
			Long reviewId = reviewOpt.map(r -> r.getReviewId()).orElse(null);
			
			return new MyReviewTripItem(
					trip.getTripId(),
					trip.getTitle(),
					trip.getRegion(),
					trip.getStartAt(),
					trip.getEndAt(),
					ended,
					canWrite,
					hasReview,
					reviewId
			);
		}).toList();
	}
}
