package datasa.controller;


import datasa.domain.dto.*;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.List;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/trip")
@Slf4j
public class TripController {
	
	/**
	 * 게시글 수정 처리
	 *
	 */
	private static final Long TEST_USER_ID = 1L;
	private final TripService tripService;
	@Value("${kakao.maps.js-key}")
	private String kakaoJsKey;
	
	// json api 여행리스트
	@GetMapping("/mainList")
	public String mainList() {
		return "trip-test";
	}
	
	//신청페이지 만든다고 만든 여행상세페이지
	@GetMapping("/{tripId}")
	public String detailView(@PathVariable Long tripId, Model model) {
		model.addAttribute("tripId", tripId);
		return "trip-detail"; // templates/trip-detail.html
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
		model.addAttribute("jsKey", kakaoJsKey);
		return "trip/writeForm";
	}
	
	
	@PostMapping("/write")
	public String write(@ModelAttribute TripWriteRequest request, Model model, Authentication authentication) {
		System.out.println("신청자 계정 클래스 정보 : " + authentication.getPrincipal().getClass());
		System.out.println(authentication.getPrincipal());
		
		try {
			Object principal = authentication.getPrincipal();
			String email;
			
			if (principal instanceof UserDetails userDetails) {
				email = userDetails.getUsername();
			} else {
				// 혹시 principal이 String(email)로 들어올 경우도 대비
				// 근데 필요 없을 거 같긴함
				email = String.valueOf(principal);
			}
			tripService.write(email, request);
			return "redirect:/api/trip/listAll";
		} catch (Exception e) {
			e.printStackTrace();
			
			if (request.getStartAt() == null) {
				request.setStartAt(LocalDateTime.now());
			}
			if (request.getEndAt() == null) {
				request.setEndAt(LocalDateTime.now().plusDays(3));
			}
			
			model.addAttribute("request", request);
			model.addAttribute("jsKey", kakaoJsKey);
			return "trip/writeForm";
		}
	}
	
	
	@GetMapping("/update/{id}")
	public String updateForm(@PathVariable Long id, Model model) {
		
		TripDetailResponse detail = tripService.getTripDetail(id);
		
		// 작성자 검증
		if (!detail.getHostUser().getUserId().equals(1L)) {
			return "redirect:/api/trip/listAll";
		}
		
		TripUpdateRequest req = new TripUpdateRequest();
		req.setTripId(detail.getTripId());
		req.setTitle(detail.getTitle());
		req.setDescription(detail.getDescription());
		req.setRegion(detail.getRegion());
		req.setEstimatedCost(detail.getEstimatedCost());
		req.setMaxParticipants(detail.getMaxParticipants());
		req.setDurationMinutes(detail.getDurationMinutes());
		req.setStartAt(detail.getStartAt());
		req.setEndAt(detail.getEndAt());
		req.setTheme(detail.getTheme());
		
		// ✅ TripLocation → schedulePlaces 변환
		if (detail.getLocations() != null) {
			req.setSchedulePlaces(
					detail.getLocations().stream()
							.map(l -> TripWriteSchedulePlaceRequest.builder()
									.placeId(l.getPlaceId())
									.placeName(l.getPlaceName())
									.address(l.getAddress())
									.lat(l.getLat())
									.lng(l.getLng())
									.build())
							.toList()
			);
		}
		
		model.addAttribute("request", req);
		model.addAttribute("jsKey", kakaoJsKey);
		
		return "trip/updateForm";
	}
	
	@PostMapping("/update")
	public String update(@ModelAttribute("request") TripUpdateRequest request) {
		
		tripService.updateTrip(request, 1L); // 임시 로그인 유저
		return "redirect:/api/trip/detail/" + request.getTripId();
	}
	
	
	//	게시글 (상세)읽기
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Long id, Model model) {
		TripDetailResponse response = tripService.getTripDetail(id);
		model.addAttribute("trip", response);
		return "trip/detail";
	}
	
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id) {
		try {
			tripService.deleteTrip(id, 1L); // 임시 : 테스트용 로그인 유저
			return "redirect:/api/trip/listAll";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/api/trip/detail/" + id;
		}
	}
	
}
