package datasa.controller;

import datasa.dto.ApplicationCreateResponseDto;
import datasa.dto.ApplicationStatusResponseDto;
import datasa.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    /**
     * U_005 내 신청 상태 조회
     */
    @GetMapping("/trips/{tripId}/status")
    public ApplicationStatusResponseDto myStatus(
            @PathVariable Long tripId,
            @RequestParam Long userId
    ) {
        return applicationService.getMyApplicationStatus(tripId, userId);
    }
}

