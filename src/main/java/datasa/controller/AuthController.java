package datasa.controller;

import datasa.domain.dto.AuthResponse;
import datasa.domain.dto.SignupRequest;
import datasa.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@GetMapping("/me")
	public ResponseEntity<Void> me() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth == null || !auth.isAuthenticated()
				|| "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
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
	
	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		
		ResponseCookie access = ResponseCookie.from("access_token", "")
				.httpOnly(true)
				.path("/")
				.sameSite("Lax")
				.maxAge(0)
				.build();
		
		ResponseCookie jsession = ResponseCookie.from("JSESSIONID", "")
				.path("/")
				.maxAge(0)
				.build();
		
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, access.toString())
				.header(HttpHeaders.SET_COOKIE, jsession.toString())
				.build();
	}
	
	@DeleteMapping("/withdraw")
	public ResponseEntity<Void> withdraw(
			@AuthenticationPrincipal UserDetails userDetails
	) {
		authService.withdraw(userDetails.getUsername());
		
		// 🔥 access_token 쿠키 삭제
		ResponseCookie access = ResponseCookie.from("access_token", "")
				.httpOnly(true)
				.path("/")
				.sameSite("Lax")
				.maxAge(0)
				.build();
		
		return ResponseEntity.noContent()
				.header(HttpHeaders.SET_COOKIE, access.toString())
				.build();
	}
	
	
	
}
