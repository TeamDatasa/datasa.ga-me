package datasa.controller;

import datasa.service.MyPageUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/tours")
public class MyToursController {
	
	private final MyPageUserService myPageUserService;
	
	@GetMapping
	public String page(
			@AuthenticationPrincipal UserDetails userDetails,
			Model model
	) {
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		String email = userDetails.getUsername();
		
		model.addAttribute("likedTours",
				myPageUserService.getLikedTours(email));
		model.addAttribute("recentTours",
				myPageUserService.getRecentViewedTours(email));
		
		return "users/myTour";
	}
	
	
}

