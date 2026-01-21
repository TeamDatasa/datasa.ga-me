package datasa.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapPageController {
	
	@Value("${naver.maps.ncpKeyId}")
	private String ncpKeyId;
	
	@GetMapping("/maps")
	public String maps(Model model) {
		model.addAttribute("ncpKeyId", ncpKeyId); // 공개키만 전달
		return "maps/page";
	}
}