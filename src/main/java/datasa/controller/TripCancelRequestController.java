package datasa.controller;

import datasa.security.CustomUserDetail;
import datasa.service.TripCancelRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cancel-requests")
public class TripCancelRequestController {
	
	private final TripCancelRequestService tripCancelRequestService;
	
	@PostMapping("/applications/{applicationId}")
	public void requestCancel(
			@PathVariable Long applicationId,
			@AuthenticationPrincipal CustomUserDetail user
	) {
		if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		tripCancelRequestService.requestCancel(applicationId, user.getUserId());
	}
}