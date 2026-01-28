package datasa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PasswordResetPageController {
	@GetMapping("/reset-password")
	public String page(){
		return "reset-password";
	}
}
