package datasa.controller;

import datasa.domain.dto.ApplicationCreateResponseDto;
import datasa.domain.dto.ApplicationListResponseDto;
import datasa.security.CustomUserDetail;
import datasa.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {

	private final ApplicationService applicationService;

	@GetMapping("/trips/{tripId}")
	public List<ApplicationListResponseDto> list(
			@PathVariable Long tripId
	) {
		return applicationService.getApplicationsByTrip(tripId);
	}

	// 추가: 내가 이 trip을 신청했는지 여부(PENDING/APPROVED 포함)
	// trip-apply.js가 GET /api/applications/trips/{tripId}/me 로 호출합니다.
	@GetMapping("/trips/{tripId}/me")
	public boolean me(
			@PathVariable Long tripId,
			@AuthenticationPrincipal CustomUserDetail user
	) {
		if (user == null) {
			return false;
		}
		return applicationService.hasApplied(tripId, user.getUserId());
	}

	@GetMapping("/host/trips/{tripId}")
	public List<ApplicationListResponseDto> listByTripForHost(
			@PathVariable Long tripId,
			@RequestParam Long hostUserId
	) {
		return applicationService.getApplicationsByTripForHost(tripId, hostUserId)
				.stream()
				.map(ApplicationListResponseDto::from)
				.toList();
	}

	// 신청
	@PostMapping("/trips/{tripId}")
	public ApplicationCreateResponseDto apply(
			@PathVariable Long tripId,
			@AuthenticationPrincipal CustomUserDetail user
	) {
		Long userId = user.getUserId();

		boolean alreadyApplied = applicationService.hasApplied(tripId, userId);
		if (alreadyApplied) {
			throw new IllegalStateException("이미 신청된 여정입니다.");
		}

		return applicationService.applyTrip(tripId, userId);
	}

	@PostMapping("/{applicationId}/approve")
	public ResponseEntity<?> approve(
			@PathVariable Long applicationId,
			@RequestParam Long hostUserId
	) {
		applicationService.approve(applicationId, hostUserId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{applicationId}/reject")
	public ResponseEntity<?> reject(
			@PathVariable Long applicationId,
			@RequestParam Long hostUserId
	) {
		applicationService.reject(applicationId, hostUserId);
		return ResponseEntity.ok().build();
	}
}