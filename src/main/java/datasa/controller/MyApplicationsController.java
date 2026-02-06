package datasa.controller;

import datasa.domain.entity.Application;
import datasa.service.MyPageUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/applications")
public class MyApplicationsController {
	
	private final MyPageUserService myPageUserService;
	
	@GetMapping
	public String page(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(required = false) Application.Status status,
			Model model
	) {
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		String email = userDetails.getUsername();
		
		var applications = (status == null)
				? myPageUserService.getMyApplications(email)
				: myPageUserService.getMyApplicationsByStatus(email, status);
		
		model.addAttribute("applications", applications);
		model.addAttribute("status", status);
		return "users/mypage-application-list";
	}
}
