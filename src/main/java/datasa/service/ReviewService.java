package datasa.service;

import datasa.domain.dto.ReviewCreateRequest;
import datasa.domain.dto.ReviewResponse;
import datasa.domain.dto.ReviewUpdateRequest;
import datasa.domain.entity.Application;
import datasa.domain.entity.Review;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.ApplicationRepository;
import datasa.repository.ReviewRepository;
import datasa.repository.TripRepository;
import datasa.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {
	
	private final UserRepository userRepository;
	private final TripRepository tripRepository;
	private final ReviewRepository reviewRepository;
	private final ApplicationRepository applicationRepository;
	
	/** 후기 등록 */
	@Transactional
	public ReviewResponse create(String email, ReviewCreateRequest req) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Trip trip = tripRepository.findById(req.tripId())
				.orElseThrow(() -> new IllegalArgumentException("Trip not found"));
		
		// 1) 참여(승인)한 유저만 작성 가능
		boolean joined = applicationRepository.existsByUser_UserIdAndTrip_TripIdAndStatus(
				user.getUserId(),
				trip.getTripId(),
				Application.Status.APPROVED
		);
		if (!joined) throw new IllegalArgumentException("Not allowed: not joined");
		
		// 2) 지난 투어만 후기 가능 (endAt < now)
		if (!trip.getEndAt().isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("Not allowed: tour not ended");
		}
		
		// 3) trip당 후기 1개 제한
		if (reviewRepository.countByUser_UserIdAndTrip_TripId(user.getUserId(), trip.getTripId()) > 0) {
			throw new IllegalArgumentException("Review already exists");
		}
		
		Review saved = reviewRepository.save(
				Review.create(user, trip, req.rating(), req.content())
		);
		
		return toResponse(saved);
	}
	
	/** 후기 수정 */
	@Transactional
	public ReviewResponse update(String email, Long reviewId, ReviewUpdateRequest req) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Review not found"));
		
		// 본인 후기만 수정 가능
		if (!review.getUser().getUserId().equals(user.getUserId())) {
			throw new IllegalArgumentException("Not allowed: not your review");
		}
		
		review.update(req.rating(), req.content());
		return toResponse(review);
	}
	
	/** 후기 삭제 */
	@Transactional
	public void delete(String email, Long reviewId) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Review not found"));
		
		if (!review.getUser().getUserId().equals(user.getUserId())) {
			throw new IllegalArgumentException("Not allowed: not your review");
		}
		
		reviewRepository.delete(review);
	}
	
	private ReviewResponse toResponse(Review r) {
		return new ReviewResponse(
				r.getReviewId(),
				r.getTrip().getTripId(),
				r.getRating(),
				r.getContent(),
				r.getCreatedAt() != null ? r.getCreatedAt().toString() : null,
				r.getUpdatedAt() != null ? r.getUpdatedAt().toString() : null
		);
	}
}