package datasa.controller;

import datasa.domain.dto.HostApplicationItem;
import datasa.security.CustomUserDetail;
import datasa.service.HostTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/host/trips")
public class HostApplicationApiController {
	
	private final HostTripService hostTripService;
	
	@GetMapping("/{tripId}/applications")
	public List<HostApplicationItem> getApplications(
			@PathVariable Long tripId,
			@AuthenticationPrincipal CustomUserDetail user
	) {
		if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		return hostTripService.getTripApplications(tripId, user.getUsername());
	}
	
	@PostMapping("/{tripId}/applications/{applicationId}/approve")
	public void approve(
			@PathVariable Long tripId,
			@PathVariable Long applicationId,
			@AuthenticationPrincipal CustomUserDetail user
	) {
		if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		hostTripService.approveApplication(tripId, applicationId, user.getUsername());
	}
	
	@PostMapping("/{tripId}/applications/{applicationId}/reject")
	public void reject(
			@PathVariable Long tripId,
			@PathVariable Long applicationId,
			@AuthenticationPrincipal CustomUserDetail user
	) {
		if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		hostTripService.rejectApplication(tripId, applicationId, user.getUsername());
	}
}
