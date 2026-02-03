package datasa.controller;

import datasa.domain.dto.TripDetailResponse;
import datasa.domain.dto.TripDetailResponseDto;
import datasa.domain.dto.TripListResponseDto;
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
	
	/** U_001 */
	@GetMapping
	public Page<TripListResponseDto> list(
			@RequestParam(defaultValue = "latest") String order,
			Pageable pageable
	) {
		return tripService.getTripList(order, pageable);
	}
	
	/** U_002 */
	@GetMapping("/mainList")
	public Page<TripListResponseDto> searchTrips(
			@RequestParam(required = false) List<String> languages,
			@RequestParam(required = false) String region,
			@RequestParam(required = false) String theme,
			@RequestParam(defaultValue = "latest") String order,
			Pageable pageable
	) {
		return tripService.searchTrips(
				languages, region, theme, order, pageable
		);
	}
	
	/**
	 * U_003 여행 상세 (API)
	 * - 비로그인: applicationStatus = null
	 * - 로그인: token에서 email -> userId 조회 후 applicationStatus 계산
	 */
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
