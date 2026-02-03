package datasa.controller;

import datasa.domain.dto.TripListResponse;
import datasa.domain.dto.TripListResponseDto;
import datasa.domain.dto.TripWriteRequest;
import datasa.service.HostTripService;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/host/trips")
@RequiredArgsConstructor
public class HostTripController {
	
	@Value("${KAKAO_MAPS_JS_KEY:}")
	private String kakaoJsKey;
	
	private final HostTripService hostTripService;
	
	@GetMapping("/new")
	public String newTripForm(Model model) {
		TripWriteRequest request = new TripWriteRequest();
		request.setStartAt(LocalDateTime.now());
		request.setEndAt(LocalDateTime.now().plusDays(3));
		
		model.addAttribute("jsKey", kakaoJsKey);
		model.addAttribute("request", request);
		
		return "trip/writeForm";
	}
	
	@GetMapping
	public String myTrips(Model model) {
		Long hostUserId = 1L; // 나중에 로그인 유저로 교체
		model.addAttribute("boardList", hostTripService.getMyTrips(hostUserId));
		return "trip/listAll";
	}
}
