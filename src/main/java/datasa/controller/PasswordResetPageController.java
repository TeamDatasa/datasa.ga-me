package datasa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetPageController {
	@GetMapping("/reset-password")
	public String page(@RequestParam(required = false) String token, Model model){
		model.addAttribute("token", token);
		return "auth/reset-password";
	}
}
