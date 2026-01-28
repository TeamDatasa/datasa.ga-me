package datasa.service;

import datasa.domain.dto.ApplicationCounts;
import datasa.domain.dto.MyApplicationItem;
import datasa.domain.dto.MyPageUserDetailsResponse;
import datasa.domain.dto.MyTourItem;
import datasa.domain.entity.Application;
import datasa.domain.entity.Review;
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
@Transactional(readOnly = true)
public class MyPageUserService {
	
	private final UserRepository userRepository;
	private final ApplicationRepository applicationRepository;
	private final ReviewRepository reviewRepository;
	
	public MyPageUserDetailsResponse getUserDetails(String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Long userId = user.getUserId();
		LocalDateTime now = LocalDateTime.now();
		
		// 1) 신청 현황 카운트
		long pending = applicationRepository.countByUser_UserIdAndStatus(userId, Application.Status.PENDING);
		long approved = applicationRepository.countByUser_UserIdAndStatus(userId, Application.Status.APPROVED);
		long rejected = applicationRepository.countByUser_UserIdAndStatus(userId, Application.Status.REJECTED);
		
		ApplicationCounts counts = new ApplicationCounts(pending, approved, rejected);
		
		// 2) 신청 리스트(최근 20)
		List<MyApplicationItem> myApplications =
				applicationRepository.findTop20ByUser_UserIdOrderByApplicationIdDesc(userId)
						.stream()
						.map(a -> new MyApplicationItem(
								a.getApplicationId(),
								a.getTrip().getTripId(),
								a.getTrip().getTitle(),
								a.getStatus().name(),
								a.getMessage(),
								a.getCreatedAt() != null ? a.getCreatedAt().toString() : null
						))
						.toList();
		
		// 3) 참여 투어(APPROVED)
		List<MyTourItem> myTours =
				applicationRepository.findByUser_UserIdAndStatusOrderByApplicationIdDesc(
								userId, Application.Status.APPROVED
						)
						.stream()
						.map(a -> {
							Trip trip = a.getTrip();
							
							boolean isPast = trip.getEndAt().isBefore(now);
							
							Long reviewId = reviewRepository
									.findByUser_UserIdAndTrip_TripId(userId, trip.getTripId())
									.map(Review::getReviewId)
									.orElse(null);
							
							boolean canReview = isPast && reviewId == null;
							
							return new MyTourItem(
									trip.getTripId(),
									trip.getTitle(),
									trip.getRegion(),
									trip.getStartAt(),
									trip.getEndAt(),
									a.getStatus().name(),
									reviewId,
									canReview
							);
						})
						.toList();
		
		return new MyPageUserDetailsResponse(counts, myTours, myApplications);
	}
}
