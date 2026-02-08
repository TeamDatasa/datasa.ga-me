package datasa.controller;

import datasa.domain.dto.HostCardPublicResponse;
import datasa.domain.dto.HostCardUpdateRequest;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/host/card")
public class HostCardController {
	
	private final UserRepository userRepository;
	
	// ✅ 공개 여부 조회 (새로고침 시 체크 유지 핵심)
	@GetMapping
	public HostCardPublicResponse getPublic(Authentication authentication) {
		User user = requireUser(authentication);
		
		if (user.getRole() != User.Role.HOST) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "HOST만 가능합니다.");
		}
		
		return new HostCardPublicResponse(Boolean.TRUE.equals(user.getHostCardPublic()));
	}
	
	// ✅ 공개 여부 변경 (DB에 1 저장)
	@PatchMapping
	public HostCardPublicResponse updatePublic(
			@RequestBody HostCardUpdateRequest req,
			Authentication authentication
	) {
		User user = requireUser(authentication);
		
		if (user.getRole() != User.Role.HOST) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "HOST만 가능합니다.");
		}
		
		boolean isPublic = Boolean.TRUE.equals(req.getIsPublic());
		
		System.out.println("[PATCH host/card] email=" + user.getEmail()
				+ ", req.isPublic=" + req.getIsPublic());
		
		user.setHostCardPublic(isPublic);
		
		// 🔥 이게 핵심 (flush 안 하면 반영 안 된 것처럼 보일 수 있음)
		userRepository.saveAndFlush(user);
		
		System.out.println("[AFTER save] db.hostCardPublic=" + user.getHostCardPublic());
		
		return new HostCardPublicResponse(user.getHostCardPublic());
	}
	
	// =========================
	// 🔒 로그인 유저 보장
	// =========================
	private User requireUser(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
		}
		
		Object principal = authentication.getPrincipal();
		String email;
		
		if (principal instanceof UserDetails ud) {
			email = ud.getUsername();
		} else {
			email = String.valueOf(principal);
		}
		
		if (email == null || email.isBlank() || "anonymousUser".equals(email)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
		}
		
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유저 없음"));
	}
}
