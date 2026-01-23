package datasa.controller;

import datasa.service.NaverLocalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class MapPlaceApiController {
	
	private final NaverLocalSearchService naverLocalSearchService;
	
	@GetMapping("/places")
	public String places(@RequestParam String query,
						 @RequestParam(defaultValue = "5") int display) {
		return naverLocalSearchService.search(query, display);
	}
}
