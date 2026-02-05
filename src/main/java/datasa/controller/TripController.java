package datasa.controller;


import datasa.domain.dto.*;
import datasa.security.CustomUserDetail;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
	public String write(
			@ModelAttribute TripWriteRequest request,
			Model model,
			@AuthenticationPrincipal CustomUserDetail user
	) {
		try {
			if (user == null) {
				return "redirect:/login";
			}
			
			Long userId = user.getUserId();
			
			tripService.write(userId, request);
			
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
	public String updateForm(
			@PathVariable Long id,
			Model model,
			@AuthenticationPrincipal CustomUserDetail user
	) {
		// 1. 로그인 사용자 확인
		if (user == null) {
			return "redirect:/login";
		}
		
		Long userId = user.getUserId();
		
		// 2. 여행 상세 조회
		TripDetailResponse detail = tripService.getTripDetail(id, userId);
		
		// 3. 작성자 검증 (핵심)
		if (!detail.getHostUser().getUserId().equals(userId)) {
			return "redirect:/api/trip/listAll";
		}
		
		// 4. 수정용 Request DTO 생성
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
		
		// 5. 모델 세팅
		model.addAttribute("request", req);
		model.addAttribute("jsKey", kakaoJsKey);
		
		// 6. 수정 폼 반환
		return "trip/updateForm";
	}
	
	@PostMapping("/update")
	public String update(@ModelAttribute("request") TripUpdateRequest request) {
		
		tripService.updateTrip(request, 1L); // 임시 로그인 유저
		return "redirect:/api/trip/detail/" + request.getTripId();
	}
	
	
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Long id, Model model, Authentication authentication) {
		
		boolean isLogin = authentication != null && authentication.isAuthenticated()
				&& !(authentication.getPrincipal() instanceof String s && "anonymousUser".equals(s));
		
		Long userId = null;
		boolean isMine = false;
		
		if (isLogin && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails ud) {
			String email = ud.getUsername();
			userId = tripService.findUserIdByEmail(email);
		}
		
		TripDetailResponse response = tripService.getTripDetail(id, userId);
		model.addAttribute("trip", response);
		
		if (userId != null && response.getHostUserId() != null) {
			isMine = userId.equals(response.getHostUserId());
		}
		
		model.addAttribute("isLogin", isLogin);
		model.addAttribute("userId", userId);
		model.addAttribute("isMine", isMine);
		
		return "trip/detail";
	}


	
}
