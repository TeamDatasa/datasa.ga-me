package datasa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/host")
public class HostApplicationController {

    @GetMapping("/trips/{tripId}/applications")
    public String applicationManagePage(
            @PathVariable Long tripId,
            @RequestParam Long hostUserId,   // 테스트용
            Model model
    ) {
        model.addAttribute("tripId", tripId);
        model.addAttribute("hostUserId", hostUserId);
        return "host-application";
    }
}
