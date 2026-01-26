package datasa.controller;

import datasa.domain.dto.TripDetailResponse;
import datasa.domain.dto.TripDetailResponseDto;
import datasa.domain.dto.TripListResponseDto;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripApiController {

    private final TripService tripService;

    /** U_001 */
    @GetMapping
    public Page<TripListResponseDto> list(
            @RequestParam(defaultValue = "latest") String order,
            Pageable pageable
    ) {
        return tripService.getTripList(order, pageable);
    }

    /** U_002 */
    @GetMapping("/mainList")
    public Page<TripListResponseDto> searchTrips(
            @RequestParam(required = false) List<String> languages,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String theme,
            @RequestParam(defaultValue = "latest") String order,
            Pageable pageable
    ) {
        return tripService.searchTrips(
                languages, region, theme, order, pageable
        );
    }

    /**
     * U_003 여행 상세 (API)
     */
    @GetMapping("/{tripId}")
    public TripDetailResponseDto getTripDetail(
            @PathVariable Long tripId,
            @RequestParam Long userId
    ) {
        return tripService.getTripDetail(tripId, userId);
    }

}

