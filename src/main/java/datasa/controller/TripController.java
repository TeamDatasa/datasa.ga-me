package datasa.controller;

import datasa.entity.Trip;
import datasa.entity.User;
import datasa.service.TripService;
import domain.dto.TripDetailResponse;
import domain.dto.TripListResponse;
import domain.dto.TripUpdateRequest;
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
	/**
	 * 게시글 수정 처리
	 *
	 */
	private static final Long TEST_USER_ID = 1L;
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
	
}
