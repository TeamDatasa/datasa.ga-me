package datasa.controller;


import datasa.domain.dto.*;
import datasa.security.CustomUserDetail;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import datasa.domain.entity.Application;
import datasa.repository.ApplicationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDateTime;


@Controller
@RequiredArgsConstructor
@RequestMapping("/trip")
@Slf4j
public class TripController {

    /**
     * 게시글 수정 처리
     *
     */
    private final TripService tripService;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    @Value("${kakao.maps.js-key}")
    private String kakaoJsKey;

    @GetMapping("/listAll")
    public String listAll(
            Model model,
            @AuthenticationPrincipal CustomUserDetail user,
            @RequestParam(defaultValue = "latest") String order,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) List<String> languages,
            @RequestParam(required = false) String province
    ) {
        Long userId = (user != null) ? user.getUserId() : null;
        
        List<TripListResponse> boardList;
        
        boolean hasFilter =
                (region != null && !region.isBlank())
                        || (theme != null && !theme.isBlank())
                        || (languages != null && !languages.isEmpty())
                        || ("popular".equalsIgnoreCase(order));
        
        if (!hasFilter) {
            // 기존 동작 그대로
            boardList = tripService.getListAll(userId);
        } else {
            // 필터/정렬 적용 (사이즈 크게)
            Pageable pageable = PageRequest.of(0, 2000);
            boardList = tripService.searchTripsForMainUi(
                    languages,
                    (region != null && !region.isBlank()) ? region : null,
                    (theme != null && !theme.isBlank()) ? theme : null,
                    order,
                    pageable,
                    userId
            ).getContent();
        }
        
        model.addAttribute("boardList", boardList);
        
        // 필터 UI 유지용
        model.addAttribute("selectedOrder", order);
        model.addAttribute("selectedTheme", theme);
        model.addAttribute("selectedRegion", region);
        model.addAttribute("selectedLanguages", languages);
        model.addAttribute("selectedProvince", province);
        
        String role = null;
        if (user != null) {
            User u = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
            role = u.getRole().name();
        }
        model.addAttribute("role", role);
        
        return "trip/listAll";
    }


    // 게시글 작성 요청
    @GetMapping("/write")
    public String writeForm(Model model,
                            @AuthenticationPrincipal CustomUserDetail user,
                            @RequestParam(name = "error", required = false) String error) {
        if (user == null) {
            log.debug("로그인 해주세요 : {}", user.getUserId());
            return "redirect:/auth/login";
        }
        model.addAttribute("jsKey", kakaoJsKey);
        model.addAttribute("request", new TripWriteRequest());
        model.addAttribute("errorMessage", error);
        return "trip/writeForm";
    }

    @PostMapping("/write")
    public String write(@ModelAttribute TripWriteRequest request,
                        @AuthenticationPrincipal CustomUserDetail user) {

        if (user == null) {
            log.debug("로그인 해주세요 : {}", user.getUserId());
            return "redirect:/auth/login";
        }

        try {
            Long tripId = tripService.write(user.getUserId(), request);
            return "redirect:/trip/detail/" + tripId;
        } catch (IllegalArgumentException ex) {
            return "redirect:/trip/write?error=" + java.net.URLEncoder.encode(
                    ex.getMessage(),
                    java.nio.charset.StandardCharsets.UTF_8
            );
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
            return "redirect:/trip/listAll";
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
    public String update(
            @ModelAttribute("request") TripUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetail user
    ) {
        if (user == null) {
            return "redirect:/login";
        }
        tripService.updateTrip(request, user.getUserId());
        return "redirect:/trip/detail/" + request.getTripId();
    }

    @PostMapping("/delete/{tripId}")
    public String deleteTrip(
            @PathVariable Long tripId,
            @AuthenticationPrincipal CustomUserDetail user
    ) {
        if (user == null) {
            return "redirect:/login";
        }
        tripService.deleteTrip(tripId, user.getUsername());
        return "redirect:/trip/listAll";
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

        boolean isOwner = false;
        if (userId != null) {
            if (response.getHostUserId() != null) {
                isOwner = userId.equals(response.getHostUserId());
            } else if (response.getHostUser() != null && response.getHostUser().getUserId() != null) {
                isOwner = userId.equals(response.getHostUser().getUserId());
            }
        }

        boolean canEditDelete = false;
        if (isOwner && response.getStartAt() != null) {
            int lockDays = (response.getEditLockDays() != null) ? response.getEditLockDays() : 7;
            LocalDateTime lockAt = response.getStartAt().minusDays(lockDays);
            canEditDelete = !LocalDateTime.now().isAfter(lockAt);
        }

        // 작성자면 버튼 "노출" (기간 지났으면 비활성/문구 처리용으로 canEditDelete 전달)
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("canEditDelete", canEditDelete);

        // 추가: 신청 버튼 비활성화 조건(24시간 전 / 정원마감)
        boolean applyExtraDisabled = false;
        String applyDisabledReason = null;

        // 정원마감(승인 APPROVED 인원 기준)
        long approvedCount = applicationRepository.countByTrip_TripIdAndStatus(
                tripId, Application.Status.APPROVED
        );

        int maxGuests = response.getMaxParticipants() - 1;
        if (response.getMaxParticipants() != null && approvedCount >= maxGuests) {
            applyExtraDisabled = true;
            applyDisabledReason = "모집이 마감되었습니다.";
        }

        // 시작 24시간 전부터 신청 불가
        if (!applyExtraDisabled && response.getStartAt() != null) {
            LocalDateTime lockAt = response.getStartAt().minusHours(24);
            if (!LocalDateTime.now().isBefore(lockAt)) {
                applyExtraDisabled = true;
                applyDisabledReason = "여정 시작 24시간 전부터는 신청할 수 없습니다.";
            }
        }

        model.addAttribute("applyExtraDisabled", applyExtraDisabled);
        model.addAttribute("applyDisabledReason", applyDisabledReason);

        return "trip/detail";
    }


}
