package datasa.controller;

import datasa.domain.dto.TripReviewPageResponse;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.service.TripReviewPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripReviewPageController {
	
	private final TripReviewPageService tripReviewPageService;
	private final UserRepository userRepository;
	
	/**
	 * 유저 디테일(트립 + 리뷰 페이지)
	 * GET /api/trips/{tripId}/review-page
	 */
	@GetMapping("/{tripId}/review-page")
	public TripReviewPageResponse reviewPage(
			@PathVariable Long tripId,
			@AuthenticationPrincipal UserDetails userDetails
	) {
		
		// 로그인 유저 email
		String email = userDetails.getUsername();
		
		// 로그인 유저 ID 조회 (likedByMe, 권한 체크용)
		User loginUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Long loginUserId = loginUser.getUserId();
		
		return tripReviewPageService.getReviewPage(tripId, loginUserId, email);
	}
}
