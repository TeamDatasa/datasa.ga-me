package datasa.service;

import datasa.domain.dto.ReviewResponse;
import datasa.domain.dto.TripDetailResponse;
import datasa.domain.dto.TripReviewPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripReviewPageService {
	
	private final TripLikeService tripLikeService;
	private final ReviewService reviewService;
	
	@Transactional(readOnly = true)
	public TripReviewPageResponse getReviewPage(Long tripId, Long loginUserId, String email) {
		
		// 1) Trip 디테일 (평점 포함)
		TripDetailResponse trip = tripLikeService.getTripDetail(tripId, loginUserId);
		
		// 2) 전체 리뷰 리스트
		List<ReviewResponse> reviews = reviewService.listByTrip(tripId);
		
		// 3) 작성 가능 여부
		boolean canWrite = reviewService.canWriteReview(email, tripId);
		
		// 4) 내 리뷰 (없으면 null)
		ReviewResponse myReview = null;
		try {
			myReview = reviewService.myReview(email, tripId);
		} catch (IllegalArgumentException e) {
		}
		
		return new TripReviewPageResponse(trip, canWrite, myReview, reviews);
	}
}
