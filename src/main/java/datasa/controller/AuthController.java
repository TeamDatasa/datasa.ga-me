package datasa.controller;

import datasa.domain.dto.AuthResponse;
import datasa.domain.dto.SignupRequest;
import datasa.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@GetMapping("/api/auth/me")
	public ResponseEntity<Void> me() {
		return ResponseEntity.ok().build();
	}
	
	
	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
		authService.signup(request);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody datasa.dto.LoginRequest request) {
		
		AuthResponse res = authService.login(request);
		
		ResponseCookie cookie = ResponseCookie.from("access_token", res.getAccessToken())
				.httpOnly(true)
				.path("/")
				.sameSite("Lax")
				// .secure(true) // https 쓰면 켜
				.maxAge(60 * 60 * 24) // 1 day (원하는대로)
				.build();
		
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(res);
	}
}
