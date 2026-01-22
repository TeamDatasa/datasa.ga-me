package datasa.controller;

import datasa.domain.dto.AuthResponse;
import datasa.domain.dto.SignupRequest;
import datasa.domain.dto.*;
import datasa.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
		authService.signup(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody datasa.dto.LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}
}
