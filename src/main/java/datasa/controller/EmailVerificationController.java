package datasa.controller;

import datasa.domain.dto.EmailSendCodeRequest;
import datasa.domain.dto.EmailVerifyCodeRequest;
import datasa.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController {
	
	private final EmailVerificationService emailVerificationService;
	
	@PostMapping("/send-code")
	public ResponseEntity<?> sendCode(@Valid @RequestBody EmailSendCodeRequest req) {
		try {
			emailVerificationService.sendCode(req.getEmail());
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(409)
					.body(java.util.Map.of("message", e.getMessage()));
		}
	}
	
	@PostMapping("/verify-code")
	public ResponseEntity<?> verifyCode(@Valid @RequestBody EmailVerifyCodeRequest req) {
		emailVerificationService.verifyCode(req.getEmail(), req.getCode());
		return ResponseEntity.ok(Map.of("message", "인증 완료"));
	}
}
