package datasa.controller;

import datasa.domain.dto.CommentCreateRequest;
import datasa.domain.dto.CommentResponse;
import datasa.domain.dto.CommentUpdateRequest;
import datasa.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
	
	private final CommentService commentService;
	
	// 댓글/대댓글 목록 (flat 형태, parentCommentId 포함)
	@GetMapping("/trips/{tripId}/comments")
	public List<CommentResponse> list(@PathVariable Long tripId) {
		return commentService.listByTrip(tripId);
	}
	
	// 댓글 작성 (parentCommentId가 있으면 대댓글)
	@PostMapping("/trips/{tripId}/comments")
	public CommentResponse create(@PathVariable Long tripId, @Valid @RequestBody CommentCreateRequest req) {
		return commentService.create(tripId, req);
	}
	
	// 본인 댓글 수정
	@PutMapping("/comments/{commentId}")
	public CommentResponse update(@PathVariable Long commentId, @Valid @RequestBody CommentUpdateRequest req) {
		return commentService.update(commentId, req);
	}
	
	// 본인 댓글 삭제 (status=DELETED)
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> delete(@PathVariable Long commentId, @RequestParam Long userId) {
		commentService.delete(commentId, userId);
		return ResponseEntity.noContent().build();
	}
	
	// 댓글 좋아요 토글
	@PostMapping("/comments/{commentId}/like")
	public ResponseEntity<Long> toggleLike(@PathVariable Long commentId, @RequestParam Long userId) {
		long likeCount = commentService.toggleLike(commentId, userId);
		return ResponseEntity.ok(likeCount);
	}
}
