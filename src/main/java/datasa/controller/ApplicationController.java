package datasa.controller;

import datasa.domain.dto.ApplicationCreateResponseDto;
import datasa.domain.dto.ApplicationListResponseDto;
import datasa.security.CustomUserDetail;
import datasa.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {
	
	private final ApplicationService applicationService;
	
	/**
	 * U_004 여행 신청
	 */
	@GetMapping("/trips/{tripId}")
	public List<ApplicationListResponseDto> list(
			@PathVariable Long tripId
	) {
		return applicationService.getApplicationsByTrip(tripId);
	}
	
	// 신청
	@PostMapping("/trips/{tripId}")
	public ApplicationCreateResponseDto apply(
			@PathVariable Long tripId,
			@AuthenticationPrincipal CustomUserDetail user
	) {
		Long userId = user.getUserId();
		
		try {
			boolean alreadyApplied = applicationService.hasApplied(tripId, userId);
			
			if (alreadyApplied) {
				throw new IllegalStateException("이미 신청된 여정입니다.");
			}
			
			return applicationService.applyTrip(tripId, userId);
			
		} catch (IllegalStateException e) {
			throw e;
		}
	}
	
	
	@PostMapping("/{applicationId}/approve")
	public void approve(
			@PathVariable Long applicationId,
			@RequestParam Long hostUserId
	) {
		applicationService.approve(applicationId, hostUserId);
	}
	
	@PostMapping("/{applicationId}/reject")
	public void reject(
			@PathVariable Long applicationId,
			@RequestParam Long hostUserId
	) {
		applicationService.reject(applicationId, hostUserId);
	}
	
	
}

