package datasa.controller;

import datasa.service.TripService;
import domain.dto.TripListResponse;
import domain.dto.TripWriteRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
@Slf4j
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
    public Page<Trip> search(
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "latest") String order,
            Pageable pageable
    ) {
        return tripService.searchByFilters(
                language, order, pageable
        );
    }

    // 동식ver List
	// @GetMapping("/list")
	// public String list(Model model) {
	// 	List<TripListResponse> boardList = tripService.getListAll();
	// 	model.addAttribute("boardList", boardList);
	// 	return "trips/list";
	// }
	
	@GetMapping("/write")
	public String write(@ModelAttribute TripWriteRequest request, Model model) {
		model.addAttribute("request", request);
		return "trips/writeForm";
	}
	
	@PostMapping("/write")
	public String write(@ModelAttribute TripWriteRequest request) {
		try {
			tripService.write(1L, request);
			return "redirect:/trips/list";
		} catch (Exception e) {
			e.printStackTrace();
			return "trips/writeForm";
		}
	}
	
	
}
