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
    public String main(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, Model model) {
        List<TripRecommendationDto> recommendCourses;
        Long loginMemberId =
                (Long) session.getAttribute("loginMemberId");

        boolean isLogin = (loginMemberId != null);

        // 🔥 로그인 여부 분기
        if (userDetails != null) {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow();
            recommendCourses = recommendationService.recommend(user.getUserId());
        } else {
            recommendCourses = recommendationService.getDefaultTrips();
        }
        model.addAttribute("recommendCourses", recommendCourses);
        model.addAttribute("isLogin", isLogin);

        // return "trip-test";
		return "main";
    }

}

