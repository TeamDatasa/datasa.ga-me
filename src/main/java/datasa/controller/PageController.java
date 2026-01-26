package datasa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
	@GetMapping("/mypage")
	public String mypage() {
		return "users/mypage";
	}
	
	@GetMapping("/mypage/details")
	public String mypageDetails(){
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
}
