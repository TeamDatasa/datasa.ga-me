package datasa.controller;

import datasa.entity.Trip;
import datasa.entity.User;
import datasa.service.TripService;
import domain.dto.TripDetailResponse;
import domain.dto.TripWriteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
	
	
	@PostMapping("update/{boardNum}")
	public String update(Model model, @PathVariable Long boardNum, User user
	) {
		try {
			TripDetailResponse response = tripService.getTripDetail(boardNum);
			if (!user.getUserId().equals(response.getHostUser().getUserId())) {
				throw new RuntimeException("수정권한이 없습니다.");
			}
			
			model.addAttribute("request", response);
			return "trips/updateForm";
			
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/trips/list";
		}
	}
	
	/**
	 * 게시글 수정 처리
	 *
	 */
	@PostMapping("update")
	public String update(
			@ModelAttribute("request") TripDetailResponse request, User user
	) {
		try {
			tripService.update(request, user);
			log.debug("수정이 완료 되었습니다.");
			return "redirect:/trips/read/" + request.getTripId();
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/trips/list";
		}
		
	}
}
