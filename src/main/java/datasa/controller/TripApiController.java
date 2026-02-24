// src/main/java/datasa/controller/TripApiController.java

package datasa.controller;

import datasa.domain.dto.TripDetailResponseDto;
import datasa.domain.dto.TripListResponse;
import datasa.security.CustomUserDetail;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripApiController {
	
	private final TripService tripService;
	
	/** U_001 (메인 카드 UI용) */
	@GetMapping
	public Page<TripListResponse> list(
			@RequestParam(defaultValue = "latest") String order,
			Pageable pageable,
			@org.springframework.security.core.annotation.AuthenticationPrincipal CustomUserDetail user
	) {
		Long userId = (user != null) ? user.getUserId() : null;
		return tripService.getTripListForMainUi(order, pageable, userId);
	}
	
	/** U_002 (메인 카드 UI용 필터) */
	@GetMapping("/mainList")
	public Page<TripListResponse> searchTrips(
			@RequestParam String order,
			@RequestParam(required = false) String region,
			@RequestParam(required = false) String theme,
			@RequestParam(required = false) List<String> languages,
			Pageable pageable,
			@org.springframework.security.core.annotation.AuthenticationPrincipal CustomUserDetail user
	) {
		Long userId = (user != null) ? user.getUserId() : null;
		return tripService.searchTripsForMainUi(languages, region, theme, order, pageable, userId);
	}
	
	/** U_003 여행 상세(API) 기존 유지 */
	@GetMapping("/{tripId}")
	public TripDetailResponseDto getTripDetail(
			@PathVariable Long tripId,
			Authentication authentication
	) {
		Long loginUserId = null;
		
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();
			String email = null;
			
			if (principal instanceof UserDetails userDetails) {
				email = userDetails.getUsername();
			} else if (principal != null) {
				email = String.valueOf(principal);
			}
			
			if (email != null && !email.isBlank() && !"anonymousUser".equals(email)) {
				loginUserId = tripService.findUserIdByEmail(email);
			}
		}
		
		return tripService.getTripDetail_jiwon(tripId, loginUserId);
	}
}