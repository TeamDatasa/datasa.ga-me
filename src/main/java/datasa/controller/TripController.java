
package datasa.controller;



import datasa.domain.dto.TripListResponseDto;
import datasa.service.TripService;
import datasa.domain.dto.TripDetailResponse;
import datasa.domain.dto.TripListResponse;
import datasa.domain.dto.TripUpdateRequest;
import datasa.domain.dto.TripWriteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/trip")
@Slf4j
public class TripController {
	/**
	 * 게시글 수정 처리
	 *
	 */
	private static final Long TEST_USER_ID = 1L;
	private final TripService tripService;
	
	/**
	 * 여행 목록
	 */
//	@GetMapping
//	public Page<Trip> list(
//			@RequestParam(defaultValue = "latest") String order,
//			Pageable pageable
//	) {
//		return tripService.getTripList(order, pageable);
//	}
	
	/**
	 * U_002: 여행 검색 (지역/언어/테마)
	 */
//	@GetMapping("/search")
//	public Page<Trip> search(
//			@RequestParam(required = false) String language,
//			@RequestParam(defaultValue = "latest") String order,
//			Pageable pageable
//	) {
//		return tripService.searchTrips(
//				language, order, pageable
//		);
//	}
	
//	@GetMapping("/search")
//	public Page<TripListResponseDto> search(
//			@RequestParam(required = false) String language,
//			@RequestParam(required = false) String region,
//			@RequestParam(required = false) String theme,
//			@RequestParam(defaultValue = "latest") String order,
//			Pageable pageable
//	) {
//		List<String> languages = (language == null || language.isBlank())
//				? null
//				: List.of(language);
//
//		return tripService.searchTrips(
//				languages, region, theme, order, pageable
//		);
//	}
	
	//	 동식ver List
	@GetMapping("/listAll")
	public String listAll(Model model) {
		List<TripListResponse> boardList = tripService.getListAll();
		model.addAttribute("boardList", boardList);
		return "trip/listAll";
	}
	
	@GetMapping("/write")
	public String wrtieForm(@ModelAttribute TripWriteRequest request, Model model) {
		
		if (request.getStartAt() == null) {
			request.setStartAt(LocalDateTime.now());
		}
		if (request.getEndAt() == null) {
			request.setEndAt(LocalDateTime.now().plusDays(3));
		}
		
		model.addAttribute("request", request);
		return "trip/writeForm";
	}

	@PostMapping("/write")
	public String write(@ModelAttribute TripWriteRequest request, Model model) {
		try {
			// 임시
			// 1번 사용자가 임의로 글을 작성함
			tripService.write(1L, request);
			return "redirect:/api/trip/listAll";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("request", request);
			return "trip/writeForm";
		}
	}
	
	
	
	@GetMapping("/update/{id}")
	public String updateForm(@PathVariable Long id, Model model) {
		
		TripDetailResponse detail = tripService.getTripDetail(id);
		
		// 임시 로그인 유저
		if (!detail.getHostUser().getUserId().equals(1L)) {
			return "redirect:/api/trip/listAll";
		}
		
		TripUpdateRequest req = new TripUpdateRequest();
		req.setTripId(detail.getTripId());
		req.setTitle(detail.getTitle());
		req.setDescription(detail.getDescription());
		req.setEstimatedCost(detail.getEstimatedCost());
		req.setMaxParticipants(detail.getMaxParticipants());
		req.setDurationMinutes(detail.getDurationMinutes());
		req.setStartAt(detail.getStartAt());
		req.setEndAt(detail.getEndAt());
		req.setTheme(detail.getTheme());
		
		model.addAttribute("request", req);
		return "trip/updateForm";
	}
	
	
	@PostMapping("/update")
	public String update(@ModelAttribute("request") TripUpdateRequest request) {
		
		tripService.updateTrip(request, 1L); // 임시 로그인 유저
		return "redirect:/api/trip/read/" + request.getTripId();
	}

	
	 //	게시글 (상세)읽기
	 @GetMapping("/read/{id}")
	 public String read(@PathVariable Long id, Model model) {
	 	TripDetailResponse response = tripService.getTripDetail(id);
	 	model.addAttribute("trip", response);
	 	return "trip/read";
	 }
	
	 @PostMapping("/delete/{id}")
	 public String delete(@PathVariable Long id) {
	 	try {
	 		tripService.deleteTrip(id, 1L); // 임시 : 테스트용 로그인 유저
	 		return "redirect:/api/trip/listAll";
	 	} catch (Exception e) {
	 		e.printStackTrace();
	 		return "redirect:/api/trip/read/" + id;
	 	}
	 }


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
    public TripDetailResponse getTripDetail(
            @PathVariable Long tripId
    ) {
        return tripService.getTripDetail(tripId);
    }
}
