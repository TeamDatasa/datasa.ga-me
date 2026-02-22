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
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
	
	private final UserRepository userRepository;
	private final TripRepository tripRepository;
	private final ReviewRepository reviewRepository;
	private final ApplicationRepository applicationRepository;
	
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
		
		// 2) 종료된 투어만 후기 가능
		if (!trip.getEndAt().isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("Not allowed: tour not ended");
		}
		
		// 3) trip당 1개 제한 (빠른 체크) + 유니크가 최종 방어
		if (reviewRepository.existsByUser_UserIdAndTrip_TripId(user.getUserId(), trip.getTripId())) {
			throw new IllegalArgumentException("Review already exists");
		}
		
		Review saved;
		try {
			saved = reviewRepository.save(Review.create(user, trip, req.rating(), req.content()));
		} catch (DataIntegrityViolationException e) {
			throw new IllegalArgumentException("Review already exists");
		}
		
		return toResponse(saved);
	}
	
	@Transactional
	public ReviewResponse update(String email, Long reviewId, ReviewUpdateRequest req) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Review not found"));
		
		if (!review.getUser().getUserId().equals(user.getUserId())) {
			throw new IllegalArgumentException("Not allowed: not your review");
		}
		
		review.update(req.rating(), req.content());
		return toResponse(review);
	}
	
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
	
	@Transactional(readOnly = true)
	public List<ReviewResponse> listByTrip(Long tripId) {
		return reviewRepository.findAllByTrip_TripIdOrderByCreatedAtDesc(tripId).stream()
				.map(this::toResponse)
				.toList();
	}
	
	@Transactional(readOnly = true)
	public ReviewResponse myReview(String email, Long tripId) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Review r = reviewRepository.findByUser_UserIdAndTrip_TripId(user.getUserId(), tripId)
				.orElseThrow(() -> new IllegalArgumentException("Review not found"));
		
		return toResponse(r);
	}
	
	@Transactional(readOnly = true)
	public boolean canWriteReview(String email, Long tripId) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Trip trip = tripRepository.findById(tripId)
				.orElseThrow(() -> new IllegalArgumentException("Trip not found"));
		
		boolean joined = applicationRepository.existsByUser_UserIdAndTrip_TripIdAndStatus(
				user.getUserId(), tripId, Application.Status.APPROVED
		);
		if (!joined) return false;
		
		if (!trip.getEndAt().isBefore(LocalDateTime.now())) return false;
		
		return !reviewRepository.existsByUser_UserIdAndTrip_TripId(user.getUserId(), tripId);
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
