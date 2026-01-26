package datasa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthPageController {
	
	@GetMapping("/login")
	public String loginPage(){
		return "users/login";
	}
	
	@GetMapping("/signup")
	public String signupPage(){
		return "users/signup";
	}
}
