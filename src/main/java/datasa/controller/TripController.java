package datasa.controller;

import datasa.entity.Trip;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;


    /**
     * 여행 목록
     */
    @GetMapping
    public Page<Trip> list(
            @RequestParam(defaultValue = "latest") String order,
            Pageable pageable
    ) {
        return tripService.getTripList(order, pageable);
    }


    /**
     * U_002: 여행 검색 (지역/언어/테마)
     */
    @GetMapping("/search")
    public Page<Trip> searchTrips(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String theme,
            @RequestParam(defaultValue = "latest") String order,
            Pageable pageable
    ) {
        return tripService.searchTrips(
                language, region, theme, order, pageable
        );
    }
}
