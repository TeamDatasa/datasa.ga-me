package datasa.controller;


import datasa.domain.dto.TripRecommendationDto;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;
	@GetMapping("/")
	public String main(@AuthenticationPrincipal UserDetails userDetails,
					   HttpSession session,
					   Model model) {
		
		boolean isLogin = (userDetails != null);
		model.addAttribute("isLogin", isLogin);
		
		List<TripRecommendationDto> recommendCourses;
		
		if (isLogin) {
			User user = userRepository.findByEmail(userDetails.getUsername())
					.orElseThrow();
			
			model.addAttribute("nickname", user.getName());
			
			recommendCourses = recommendationService.recommend(user.getUserId());
		} else {
			recommendCourses = recommendationService.getDefaultTrips();
		}
		
		model.addAttribute("recommendCourses", recommendCourses);
		return "main";
	}
	
	
}

