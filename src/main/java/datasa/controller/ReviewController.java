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
	
	
	@PostMapping
	public ReviewResponse create(@AuthenticationPrincipal UserDetails userDetails,
								 @Valid @RequestBody ReviewCreateRequest req) {
		String email = userDetails.getUsername();
		return reviewService.create(email, req);
	}
	
	
	@PutMapping("/{reviewId}")
	public ReviewResponse update(@AuthenticationPrincipal UserDetails userDetails,
								 @PathVariable Long reviewId,
								 @Valid @RequestBody ReviewUpdateRequest req) {
		String email = userDetails.getUsername();
		return reviewService.update(email, reviewId, req);
	}
	
	@DeleteMapping("/{reviewId}")
	public void delete(@AuthenticationPrincipal UserDetails userDetails,
					   @PathVariable Long reviewId) {
		String email = userDetails.getUsername();
		reviewService.delete(email, reviewId);
	}
	
	@GetMapping("/trips/{tripId}")
	public List<ReviewResponse> listByTrip(@PathVariable Long tripId) {
		return reviewService.listByTrip(tripId);
	}
	
	@GetMapping("/trips/{tripId}/me")
	public ReviewResponse myReview(@AuthenticationPrincipal UserDetails userDetails,
								   @PathVariable Long tripId) {
		String email = userDetails.getUsername();
		return reviewService.myReview(email, tripId);
	}
	
	@GetMapping("/trips/{tripId}/can-write")
	public boolean canWrite(@AuthenticationPrincipal UserDetails userDetails,
							@PathVariable Long tripId) {
		String email = userDetails.getUsername();
		return reviewService.canWriteReview(email, tripId);
	}
}
