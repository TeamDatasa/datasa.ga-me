package datasa.controller;

import datasa.domain.dto.MyHostedTripDto;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.service.MyPageHostService;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageHostController {

    private final UserRepository userRepository;
    private final MyPageHostService myPageHostService;
    private final TripService tripService;
    @GetMapping("/host")
    public String hostMyPage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // 로그인 유저
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Long hostUserId = user.getUserId();

        List<Trip> hostedTours = tripService.getTripsByHost(hostUserId);

        model.addAttribute("hostedTours", hostedTours);

        return "users/mypage-host";
    }
}
