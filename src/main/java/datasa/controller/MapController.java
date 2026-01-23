package datasa.controller;

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
	
	@Value("${naver.maps.client-id}")
	private String naverMapsClientId;
	
	@GetMapping("/main")
	public String mapMain(Model model, @Value("${naver.maps.client-id}") String clientId)
	{
		model.addAttribute("naverMapsClientId", clientId);
		return "map/main";
	}

	
}
