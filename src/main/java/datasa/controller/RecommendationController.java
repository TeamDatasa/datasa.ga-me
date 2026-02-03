package datasa.controller;

import datasa.domain.dto.TripRecommendationDto;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    @GetMapping("/recommendations")
    public List<TripRecommendationDto> recommend() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // 🔥 로그인 안 된 경우 (DEFAULT)
        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserDetails)) {

            System.out.println("✅ DEFAULT 추천 경로 진입");
            return recommendationService.getDefaultTrips();
        }

        // 🔥 로그인 된 경우
        System.out.println("🤖 LOGIN 추천 경로 진입");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        return recommendationService.recommend(user.getUserId());
    }


}
