package datasa.controller;

import datasa.domain.dto.PasswordResetConfirmRequest;
import datasa.domain.dto.PasswordResetRequest;
import datasa.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/password")
public class PasswordResetController {
	
	private final PasswordResetService passwordResetService;
	
	@PostMapping("/reset-request")
	public ResponseEntity<Void> request(@Valid @RequestBody PasswordResetRequest req) {
		passwordResetService.requestReset(req.getEmail());
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/reset")
	public ResponseEntity<?> reset(@Valid @RequestBody PasswordResetConfirmRequest req) {
		passwordResetService.confirmReset(req);
		return ResponseEntity.ok().build();
	}
}
