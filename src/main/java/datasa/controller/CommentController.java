package datasa.controller;

import datasa.domain.dto.CommentCreateRequest;
import datasa.domain.dto.CommentResponse;
import datasa.domain.dto.CommentUpdateRequest;
import datasa.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
	
	private final CommentService commentService;
	
	// 댓글/대댓글 목록 (비로그인 허용)
	@GetMapping("/trips/{tripId}/comments")
	public List<CommentResponse> list(@PathVariable Long tripId) {
		return commentService.listByTrip(tripId);
	}
	
	// 댓글 작성 (로그인 필수)
	@PostMapping("/trips/{tripId}/comments")
	public CommentResponse create(
			@PathVariable Long tripId,
			@Valid @RequestBody CommentCreateRequest req,
			Authentication authentication
	) {
		String email = requireLoginEmail(authentication);
		return commentService.create(tripId, email, req);
	}
	
	// 본인 댓글 수정 (로그인 + 본인)
	@PutMapping("/comments/{commentId}")
	public CommentResponse update(
			@PathVariable Long commentId,
			@Valid @RequestBody CommentUpdateRequest req,
			Authentication authentication
	) {
		String email = requireLoginEmail(authentication);
		return commentService.update(commentId, email, req);
	}
	
	// 본인 댓글 삭제 (로그인 + 본인)
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> delete(
			@PathVariable Long commentId,
			Authentication authentication
	) {
		String email = requireLoginEmail(authentication);
		commentService.delete(commentId, email);
		return ResponseEntity.noContent().build();
	}
	
	// 댓글 좋아요 토글 (로그인 필수)
	@PostMapping("/comments/{commentId}/like")
	public ResponseEntity<Long> toggleLike(
			@PathVariable Long commentId,
			Authentication authentication
	) {
		String email = requireLoginEmail(authentication);
		long likeCount = commentService.toggleLike(commentId, email);
		return ResponseEntity.ok(likeCount);
	}
	
	private String requireLoginEmail(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
		}
		
		Object principal = authentication.getPrincipal();
		String email;
		
		if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
			email = userDetails.getUsername();
		} else {
			email = String.valueOf(principal);
		}
		
		if (email == null || email.isBlank() || "anonymousUser".equals(email)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
		}
		
		return email;
	}
}
