package datasa.controller;

import datasa.dto.SignupRequest;
import datasa.entity.User;
import datasa.repository.UserRepository;
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
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	@PostMapping("/signup")
	public String signup(
			@Valid SignupRequest request,
			BindingResult bindingResult,
			Model model
			){
				/* ===== 형식/길이 검증 ===== */
				if (bindingResult.hasErrors()){
					return "signup";
				}
				/* ===== 이메일 중복 체크 ===== */
				if (userRepository.existsByEmail(request.getEmail())){
					model.addAttribute("error","이미 사용 중인 이메일입니다.");
					return "signup";
				}
				/* ===== 계정 생성 ===== */
				User user = new User(
						request.getEmail(),
						passwordEncoder.encode(request.getPassword()),
						request.getName()
				);
				userRepository.save(user);
				
				return "redirect:/login";
		
	}
	
	@GetMapping("/signup")
	public String signupForm(Model model){
		model.addAttribute("signupRequest", new SignupRequest());
		return "signup";
	}
}
