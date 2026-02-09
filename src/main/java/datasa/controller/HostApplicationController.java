package datasa.controller;

import datasa.domain.dto.HostApplicationItem;
import datasa.security.CustomUserDetail;
import datasa.service.HostTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/host/trips")
public class HostApplicationController {
	
	@GetMapping("/{tripId}/applications")
	public String applicationManagePage(@PathVariable Long tripId, Model model) {
		model.addAttribute("tripId", tripId);
		return "host-application";
	}
}
