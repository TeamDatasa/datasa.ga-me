package datasa.controller;

import datasa.domain.dto.ReviewCreateRequest;
import datasa.domain.dto.ReviewResponse;
import datasa.domain.dto.ReviewUpdateRequest;
import datasa.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
	
	private final ReviewService reviewService;
	
	/**
	 * ✅ 후기 작성
	 * POST /api/reviews
	 * body: { tripId, rating, content }
	 */
	@PostMapping
	public ReviewResponse create(@AuthenticationPrincipal UserDetails userDetails,
								 @Valid @RequestBody ReviewCreateRequest req) {
		String email = userDetails.getUsername();
		return reviewService.create(email, req);
	}
	
	/**
	 * ✅ 후기 수정 (본인만)
	 * PUT /api/reviews/{reviewId}
	 * body: { rating, content }
	 */
	@PutMapping("/{reviewId}")
	public ReviewResponse update(@AuthenticationPrincipal UserDetails userDetails,
								 @PathVariable Long reviewId,
								 @Valid @RequestBody ReviewUpdateRequest req) {
		String email = userDetails.getUsername();
		return reviewService.update(email, reviewId, req);
	}
	
	/**
	 * ✅ 후기 삭제 (본인만)
	 * DELETE /api/reviews/{reviewId}
	 */
	@DeleteMapping("/{reviewId}")
	public void delete(@AuthenticationPrincipal UserDetails userDetails,
					   @PathVariable Long reviewId) {
		String email = userDetails.getUsername();
		reviewService.delete(email, reviewId);
	}
	
	/**
	 * ✅ 특정 Trip의 후기 리스트
	 * GET /api/reviews/trips/{tripId}
	 */
	@GetMapping("/trips/{tripId}")
	public List<ReviewResponse> listByTrip(@PathVariable Long tripId) {
		return reviewService.listByTrip(tripId);
	}
	
	/**
	 * ✅ 내가 해당 Trip에 작성한 후기 1개
	 * GET /api/reviews/trips/{tripId}/me
	 */
	@GetMapping("/trips/{tripId}/me")
	public ReviewResponse myReview(@AuthenticationPrincipal UserDetails userDetails,
								   @PathVariable Long tripId) {
		String email = userDetails.getUsername();
		return reviewService.myReview(email, tripId);
	}
	
	/**
	 * ✅ 후기 작성 가능 여부 (유저 디테일 페이지에서 폼 보여줄지)
	 * GET /api/reviews/trips/{tripId}/can-write
	 */
	@GetMapping("/trips/{tripId}/can-write")
	public boolean canWrite(@AuthenticationPrincipal UserDetails userDetails,
							@PathVariable Long tripId) {
		String email = userDetails.getUsername();
		return reviewService.canWriteReview(email, tripId);
	}
}
