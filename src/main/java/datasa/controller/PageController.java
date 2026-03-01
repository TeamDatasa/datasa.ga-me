package datasa.controller;

import datasa.domain.dto.*;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.security.JwtTokenProvider;
import datasa.service.MyPageService;
import datasa.service.MyPageUserService;
import datasa.service.TripService;
import datasa.service.UserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageController {
	
	private final UserRepository userRepository;
	private final MyPageUserService myPageUserService;
	private final UserRoleService userRoleService;
	private final JwtTokenProvider jwtTokenProvider;
	private final TripService tripService;
	
	
	@GetMapping("/login")
	public String loginPage() {
		return "users/login";
	}

	
	@GetMapping("/signup")
	public String signupPage() {
		return "users/signup";
	}
	
	
	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		String email = userDetails.getUsername();
		User user = userRepository.findByEmail(email).orElseThrow();
		
		model.addAttribute("user", user);
		model.addAttribute("role", user.getRole().name());
		
		return "users/mypage";
	}
	
	@GetMapping("/mypage/details")
	public String details(@AuthenticationPrincipal UserDetails userDetails) {
		String email = userDetails.getUsername();
		User user = userRepository.findByEmail(email).orElseThrow();
		
		if (user.getRole() == User.Role.HOST) {
			return "redirect:/mypage/details/host";
		}
		return "redirect:/mypage/details/user";
	}
	
	@GetMapping("/mypage/details/user")
	public String detailsUser(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(defaultValue = "0") int tourPage,
			@RequestParam(defaultValue = "0") int likePage,
			Model model
	) {
		String email = userDetails.getUsername();
		
		MyPageUserDetailsResponse dto = myPageUserService.getUserDetails(email);
		model.addAttribute("counts", dto.counts());
		
		int pageSize = 5;
		
		// =====================
		// 내가 참여한 투어 (tourPage)
		// =====================
		List<MyTourItem> allMyTours = dto.myTours() != null ? dto.myTours() : Collections.emptyList();
		int tourTotal = allMyTours.size();
		int tourTotalPages = (int) Math.ceil((double) tourTotal / pageSize);
		
		if (tourTotalPages == 0) tourPage = 0;
		else tourPage = Math.max(0, Math.min(tourPage, tourTotalPages - 1));
		
		int tourFrom = tourPage * pageSize;
		int tourTo = Math.min(tourFrom + pageSize, tourTotal);
		
		List<MyTourItem> tourContent =
				(tourFrom >= tourTotal) ? Collections.emptyList() : allMyTours.subList(tourFrom, tourTo);
		
		model.addAttribute("myTours", tourContent);
		model.addAttribute("tourPage", tourPage);
		model.addAttribute("tourTotalPages", tourTotalPages);
		model.addAttribute("tourHasPrev", tourPage > 0);
		model.addAttribute("tourHasNext", tourPage < tourTotalPages - 1);
		
		List<Integer> tourPageNumbers = new java.util.ArrayList<>();
		if (tourTotalPages > 0) {
			int start = Math.max(0, tourPage - 2);
			int end = Math.min(tourTotalPages - 1, tourPage + 2);
			for (int i = start; i <= end; i++) tourPageNumbers.add(i);
		}
		model.addAttribute("tourPageNumbers", tourPageNumbers);
		
		// =====================
		// 내가 좋아요한 투어 (likePage)
		// =====================
		List<MyTourItem> allMyLikes = myPageUserService.getLikedTours(email);
		if (allMyLikes == null) allMyLikes = Collections.emptyList();
		
		int likeTotal = allMyLikes.size();
		int likeTotalPages = (int) Math.ceil((double) likeTotal / pageSize);
		
		if (likeTotalPages == 0) likePage = 0;
		else likePage = Math.max(0, Math.min(likePage, likeTotalPages - 1));
		
		int likeFrom = likePage * pageSize;
		int likeTo = Math.min(likeFrom + pageSize, likeTotal);
		
		List<MyTourItem> likeContent =
				(likeFrom >= likeTotal) ? Collections.emptyList() : allMyLikes.subList(likeFrom, likeTo);
		
		model.addAttribute("myLikes", likeContent);
		model.addAttribute("likePage", likePage);
		model.addAttribute("likeTotalPages", likeTotalPages);
		model.addAttribute("likeHasPrev", likePage > 0);
		model.addAttribute("likeHasNext", likePage < likeTotalPages - 1);
		
		List<Integer> likePageNumbers = new java.util.ArrayList<>();
		if (likeTotalPages > 0) {
			int start = Math.max(0, likePage - 2);
			int end = Math.min(likeTotalPages - 1, likePage + 2);
			for (int i = start; i <= end; i++) likePageNumbers.add(i);
		}
		model.addAttribute("likePageNumbers", likePageNumbers);
		
		return "users/MyPageDetailsUser";
	}
	
	@GetMapping("/mypage/details/host")
	public String detailsHost(
			@AuthenticationPrincipal UserDetails userDetails,
			Model model
	) {
		String email = userDetails.getUsername();
		User user = userRepository.findByEmail(email).orElseThrow();
		
		if (user.getRole() != User.Role.HOST) {
			return "redirect:/mypage/details/user";
		}
		model.addAttribute("hostedTours", tripService.getTripsByHost(user.getUserId()));
		
		return "users/MyPageDetailsHost";
	}
	
	
	@GetMapping("/auth/forgot-password")
	public String forgotPassword() {
		return "auth/ForgotPassword";
	}
	
	@GetMapping("/auth/reset-password")
	public String resetPassword() {
		return "auth/ResetPassword";
	}
	
	
	@PatchMapping("/api/mypage/role")
	@ResponseBody
	public ResponseEntity<AuthResponse> updateRole(
			@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody RoleUpdateRequest req
	) {
		String email = userDetails.getUsername();
		
		userRoleService.updateRole(email, req.role());
		
		User user = userRepository.findByEmail(email).orElseThrow();
		String newToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
		
		ResponseCookie cookie = ResponseCookie.from("access_token", newToken)
				.path("/")
				.httpOnly(true)
				.sameSite("Lax")
				.build();
		
		AuthResponse body = new AuthResponse(newToken, user.getUserId(), user.getEmail(), user.getRole());
		
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(body);
	}
	
	@GetMapping("/community")
	public String community() {
		return "users/community";
	}
	
	
	
	
}
