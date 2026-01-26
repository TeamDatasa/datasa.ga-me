package datasa.controller;

import datasa.domain.dto.ApplicationCreateResponseDto;
import datasa.domain.dto.ApplicationListResponseDto;
import datasa.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * U_004 여행 신청
     */
    @PostMapping("/trips/{tripId}")
    public ApplicationCreateResponseDto apply(
            @PathVariable Long tripId,
            @RequestParam Long userId   // 👉 추후 로그인으로 대체
    ) {
        return applicationService.applyTrip(tripId, userId);
    }

    @GetMapping("/trips/{tripId}")
    public List<ApplicationListResponseDto> list(
            @PathVariable Long tripId
    ) {
        return applicationService.getApplicationsByTrip(tripId);
    }


    @PostMapping("/{applicationId}/approve")
    public void approve(
            @PathVariable Long applicationId,
            @RequestParam Long hostUserId
    ) {
        applicationService.approve(applicationId, hostUserId);
    }

    @PostMapping("/{applicationId}/reject")
    public void reject(
            @PathVariable Long applicationId,
            @RequestParam Long hostUserId
    ) {
        applicationService.reject(applicationId, hostUserId);
    }


}

