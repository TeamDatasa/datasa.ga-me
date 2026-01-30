package datasa.controller;

import datasa.domain.dto.AuthResponse;
import datasa.domain.dto.RoleUpdateRequest;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.security.JwtTokenProvider;
import datasa.service.MyPageUserService;
import datasa.service.UserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PageController {
	
	private final UserRepository userRepository;
	private final MyPageUserService myPageUserService;
	private final UserRoleService userRoleService;
	private final JwtTokenProvider jwtTokenProvider;
	
	// ====== PAGES ======
	
	@GetMapping({"/login", "/auth/login"})
	public String loginPage() {
		return "users/login";
	}
	
	@GetMapping("/signup")
	public String signupPage() {
		return "users/signup";
	}
	
	
	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		String email = userDetails.getUsername();
		User user = userRepository.findByEmail(email).orElseThrow();
		
		model.addAttribute("user", user);                // mypage에서 user.role 사용 가능
		model.addAttribute("role", user.getRole().name());
		
		return "users/mypage";
	}
	@GetMapping("/mypage/details")
	public String details(Authentication authentication) {
		boolean isHost = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_HOST"));
		
		return isHost ? "redirect:/mypage/details/host"
				: "redirect:/mypage/details/user";
	}
	
	
	@GetMapping("/mypage/details/user")
	public String detailsUser(Authentication authentication,
							  @AuthenticationPrincipal UserDetails userDetails,
							  Model model) {
		
		boolean isHost = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_HOST"));
		if (isHost) return "redirect:/mypage/details/host";
		
		String email = userDetails.getUsername();
		model.addAttribute("role", "USER");
		var dto = myPageUserService.getUserDetails(email);
		model.addAttribute("counts", dto.counts());
		model.addAttribute("myTours", dto.myTours());
		model.addAttribute("myApplications", dto.myApplications());
		return "users/MyPageDetails";
	}
	
	@GetMapping("/mypage/details/host")
	public String detailsHost(Authentication authentication, Model model) {
		
		boolean isUser = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
		if (isUser) return "redirect:/mypage/details/user";
		
		model.addAttribute("role", "HOST");
		return "users/MyPageDetails";
	}
	
	
	
	
	@GetMapping("/auth/forgot-password")
	public String forgotPassword() {
		return "auth/ForgotPassword";
	}
	
	@GetMapping("/auth/reset-password")
	public String resetPassword() {
		return "auth/ResetPassword";
	}
	
	// ====== API (같은 컨트롤러 안에서 @ResponseBody로 처리) ======
	
	@PatchMapping("/api/mypage/role")
	@ResponseBody
	public ResponseEntity<AuthResponse> updateRole(
			@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody RoleUpdateRequest req
	) {
		String email = userDetails.getUsername();
		
		userRoleService.updateRole(email, req.role());
		
		User user = userRepository.findByEmail(email).orElseThrow();
		String newToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
		
		ResponseCookie cookie = ResponseCookie.from("access_token", newToken)
				.path("/")
				.httpOnly(true)
				.sameSite("Lax")
				// .secure(true) // https면 켜고 localhost http면 주석
				.build();
		
		AuthResponse body = new AuthResponse(newToken, user.getUserId(), user.getEmail(), user.getRole());
		
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(body);
	}
}
