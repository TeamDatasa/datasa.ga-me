package datasa.controller;

import datasa.domain.dto.HostCardPublicResponse;
import datasa.domain.dto.HostCardUpdateRequest;
import datasa.domain.dto.MyPageProfileResponse;
import datasa.domain.dto.MyPageProfileUpdateRequest;
import datasa.service.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {
	
	private final MyPageService myPageService;
	
	// 조회
	@GetMapping("/profile")
	public ResponseEntity<MyPageProfileResponse> getProfile(Authentication auth) {
		String email = auth.getName();
		return ResponseEntity.ok(myPageService.getProfile(email));
	}
	
	// 수정

	@PutMapping("/profile")
	public ResponseEntity<MyPageProfileResponse> updateProfile(
			@Valid @RequestBody MyPageProfileUpdateRequest req,
			Authentication auth
	) {
		if (req.getMbti() != null) {
			req.setMbti(req.getMbti().trim());
			if (req.getMbti().isBlank()) req.setMbti(null);
			else req.setMbti(req.getMbti().toUpperCase());
		}
		String email = auth.getName();
		return ResponseEntity.ok(myPageService.updateProfile(email, req));
	}
	
	// 삭제
	@DeleteMapping
	public ResponseEntity<Void> deactivate(Authentication auth) {
		String email = auth.getName();
		myPageService.deactivateAccount(email);
		return ResponseEntity.noContent().build();
	}
	
}
