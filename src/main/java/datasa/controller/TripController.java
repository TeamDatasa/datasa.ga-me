package datasa.controller;

import datasa.entity.Trip;
import datasa.entity.User;
import datasa.service.TripService;
import domain.dto.TripDetailResponse;
import domain.dto.TripListResponse;
import domain.dto.TripWriteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/trip")
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
	public String wrtie(@ModelAttribute TripWriteRequest request, Model model) {
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
	
	
	@PostMapping("update/{boardNum}")
	public String update(Model model, @PathVariable Long boardNum, User user
	) {
		try {
			TripDetailResponse response = tripService.getTripDetail(boardNum);
			if (!user.getUserId().equals(response.getHostUser().getUserId())) {
				throw new RuntimeException("수정권한이 없습니다.");
			}
			
			model.addAttribute("request", response);
			return "trip/updateForm";
			
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/trip/listAll";
		}
	}
	
	/**
	 * 게시글 수정 처리
	 *
	 */
	@PostMapping("/update")
	public String update(
			@ModelAttribute("request") TripDetailResponse request, User user
	) {
		try {
			tripService.update(request, user);
			log.debug("수정이 완료 되었습니다.");
			return "redirect:/trip/read/" + request.getTripId();
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/trip/listAll";
		}
		
	}
}
