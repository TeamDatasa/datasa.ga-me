package datasa.controller;

import datasa.domain.dto.EmailSendCodeRequest;
import datasa.domain.dto.EmailVerifyCodeRequest;
import datasa.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController {
	
	private final EmailVerificationService emailVerificationService;
	
	@PostMapping("/send-code")
	public ResponseEntity<Void> sendCode(@Valid @RequestBody EmailSendCodeRequest req) {
		emailVerificationService.sendCode(req.getEmail());
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/verify-code")
	public ResponseEntity<Void> verifyCode(@Valid @RequestBody EmailVerifyCodeRequest req) {
		emailVerificationService.verifyCode(req.getEmail(), req.getCode());
		return ResponseEntity.ok().build();
	}
}
