package datasa.controller;

import datasa.config.KakaoMapsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/map")
@Slf4j
public class MapController {
	
	private final KakaoMapsProperties kakaoMapsProperties;
	
	@Value("${naver.maps.client-id}")
	private String naverMapsClientId;
	
	@GetMapping("/naver")
	public String naverMap(Model model, @Value("${naver.maps.client-id}") String clientId)
	{
		model.addAttribute("naverMapsClientId", clientId);
		return "map/naverMap";
	}
	
	@GetMapping("/kakao")
	public String kakaoMap(Model model)
	{
		model.addAttribute("jsKey", kakaoMapsProperties.jsKey());
		return "map/kakaoMap";
	}
	

}
