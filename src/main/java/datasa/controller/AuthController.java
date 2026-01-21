package datasa.controller;

import datasa.dto.SignupRequest;
import datasa.entity.User;
import datasa.repository.UserRepository;
import datasa.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/signup")
	public String signup(
			@Valid SignupRequest signupRequest,
			BindingResult bindingResult
			){
				/* ===== 형식/길이 검증 ===== */
				if (bindingResult.hasErrors()){
					return "signup";
				}
				/* ===== 이메일 중복 체크 ===== */
				try{
					authService.signUp(signupRequest);
					return "redirect:/login";
				}catch (IllegalArgumentException e){
					bindingResult.rejectValue("email", null, e.getMessage());
					return "signup";
				}
	}
	
	@GetMapping("/signup")
	public String signupForm(Model model){
		model.addAttribute("signupRequest", new SignupRequest());
		return "signup";
	}
}
