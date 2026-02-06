package datasa.controller;


import datasa.domain.dto.*;
import datasa.security.CustomUserDetail;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class TripController {
	
	/**
	 * 게시글 수정 처리
	 *
	 */
	private static final Long TEST_USER_ID = 1L;
	private final TripService tripService;
	private final UserRepository userRepository;
	@Value("${kakao.maps.js-key}")
	private String kakaoJsKey;
	private Authentication authentication;
	
	// json api 여행리스트
	@GetMapping("/mainList")
	public String mainList() {
		return "trip-test";
	}
	
	//	 동식ver List
	@GetMapping("/listAll")
	public String listAll(Model model) {
		
		// 글 목록
		List<TripListResponse> boardList = tripService.getListAll();
		model.addAttribute("boardList", boardList);
		
		// 로그인/role
		String role = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated()
				&& !"anonymousUser".equals(auth.getPrincipal())) {
			
			String email = auth.getName();
			User user = userRepository.findByEmail(email).orElse(null);
			if (user != null) role = user.getRole().name(); // HOST / USER
		}
		model.addAttribute("role", role);
		log.info(">>> /listAll role={}", role);
		
		return "trip/listAll";
	}
	
	@GetMapping("/api/trip/listAll")
	public String listAllAlias() {
		return "redirect:/listAll";
	}
	
	
	
	@GetMapping("/write")
	public String wrtieForm(@ModelAttribute TripWriteRequest request, Model model) {
		
		// 로그인 체크
		if (authentication == null || !authentication.isAuthenticated()
				|| "anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {
			return "redirect:/auth/login";
		}
		
		// email 구해서 유저 조회
		Object principal = authentication.getPrincipal();
		String email = (principal instanceof UserDetails ud) ? ud.getUsername() : String.valueOf(principal);
		
		User user = userRepository.findByEmail(email).orElse(null);
		if (user == null) return "redirect:/auth/login";
		
		// HOST만 가능
		if (user.getRole() != User.Role.HOST) {
			return "redirect:/mypage"; // 또는 권한 안내 페이지
		}
		
		// ✅ 카드프로필 공개 필수
		if (!user.getHostCardPublic()) {
			// 마이페이지로 보내고 안내(프론트에서 alert 띄우고 싶으면 query param)
			return "redirect:/mypage?needHostCardPublic=1";
		}
		
		if (request.getStartAt() == null) request.setStartAt(LocalDateTime.now());
		if (request.getEndAt() == null) request.setEndAt(LocalDateTime.now().plusDays(3));
		
		model.addAttribute("request", request);
		model.addAttribute("jsKey", kakaoJsKey);
		return "trip/writeForm";
	}
	
	@PostMapping("/api/trip/write")
	public String writeApi(
			@ModelAttribute TripWriteRequest request,
			Model model,
			Authentication authentication
	) {
		return write(request, model, authentication); // 기존 로직 재사용
	}
	
	
	@PostMapping("/write")
	public String write(
			@ModelAttribute TripWriteRequest request,
			Model model,
			Authentication authentication
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
	
	
	@GetMapping("/detail/{tripId}")
	public String detail(
			@PathVariable("tripId") Long tripId,
			Model model,
			@AuthenticationPrincipal CustomUserDetail user
	) {
		Long userId = (user != null) ? user.getUserId() : null;
		
		TripDetailResponse response = tripService.getTripDetail(tripId, userId);
		
		model.addAttribute("trip", response);
		model.addAttribute("currentUserId", userId);
		
		return "trip/detail";
	}
	
	@GetMapping("/api/trip/detail/{id}")
	public String detailAlias(@PathVariable Long id) {
		return "redirect:/detail/" + id;
	}
	
	
	
}
