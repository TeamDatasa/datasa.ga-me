package datasa.controller;

import domain.dto.TripDetailResponse;
import domain.dto.TripDetailResponseDto;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class TripPageController {
    private final TripService tripService;

    // u003 - 상세 페이지
    @GetMapping("trips/{tripId}")
    public String tripDetailPage(
            @PathVariable Long tripId,
            Model model
    )
    {
        TripDetailResponse trip = tripService.getTripDetail(tripId);
        model.addAttribute("trip", trip);
        return "trip-detail"; // templates/trip-detail.html
    }



}
