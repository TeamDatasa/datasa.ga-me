
package datasa.controller;

import datasa.domain.dto.TripDetailResponseDto;
import datasa.domain.dto.TripListResponseDto;
import datasa.domain.dto.*;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/trip")
@Slf4j
public class TripController {
	/**
	 * 게시글 수정 처리
	 *
	 */
	private final TripService tripService;



    /**
     * U_001 여행 목록 조회
     * - latest / popular
     */
    @GetMapping
    public Page<TripListResponseDto> list(
            @RequestParam(defaultValue = "latest") String order,
            Pageable pageable
    ) {
        return tripService.getTripList(order, pageable);
    }

    /**
     * U_002 여행 검색
     * - 언어(복수) / 지역 / 테마
     */
    @GetMapping("/search")
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
            @PathVariable Long tripId
    ) {
        return tripService.getTripDetail(tripId);
    }
}
