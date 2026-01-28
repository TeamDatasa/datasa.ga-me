package datasa.controller;

import datasa.domain.dto.RoleUpdateRequest;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.service.MyPageUserService;
import datasa.service.UserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
	
	// ====== PAGES ======
	
	@GetMapping({"/login", "/auth/login"})
	public String loginPage() {
		return "users/login"; // templates/users/login.html
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
	public String mypageDetails(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		String email = userDetails.getUsername();
		User user = userRepository.findByEmail(email).orElseThrow();
		
		String role = user.getRole().name();
		model.addAttribute("role", role);
		
		// USER면 디테일 데이터 내려주기
		if ("USER".equals(role)) {
			var dto = myPageUserService.getUserDetails(email);
			model.addAttribute("counts", dto.counts());
			model.addAttribute("myTours", dto.myTours());
			model.addAttribute("myApplications", dto.myApplications());
		}
		
		// HOST면 나중에 host dto 추가해서 model에 넣으면 됨
		
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
	public ResponseEntity<Void> updateRole(
			@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody RoleUpdateRequest req
	) {
		userRoleService.updateRole(userDetails.getUsername(), req.role());
		return ResponseEntity.ok().build();
	}
}
