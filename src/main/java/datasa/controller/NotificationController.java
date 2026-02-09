package datasa.controller;

import datasa.domain.dto.CommentResponse;
import datasa.domain.dto.NotificationResponse;
import datasa.service.CommentService;
import datasa.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
	
	private final NotificationQueryService notificationQueryService;
	private final CommentService commentService;
	
	
	// 최근 알림 목록
	@GetMapping
	public List<NotificationResponse> recent(Authentication authentication,
											 @RequestParam(name = "limit", defaultValue = "10") int limit) {
		return notificationQueryService.recent(authentication, limit);
	}
	
	// 읽지 않은 알림 개수
	@GetMapping("/unread-count")
	public Map<String, Long> unreadCount(Authentication authentication) {
		return Map.of("unreadCount", notificationQueryService.unreadCount(authentication));
	}
	
	// 알림 삭제(단건)
	@DeleteMapping("/{notificationId}")
	public ResponseEntity<Void> deleteOne(
			Authentication authentication,
			@PathVariable Long notificationId
	) {
		notificationQueryService.deleteOne(authentication, notificationId);
		return ResponseEntity.noContent().build();
	}
	
	
	// 모두 읽음 처리
	@PostMapping("/read-all")
	public ResponseEntity<Void> readAll(Authentication authentication) {
		notificationQueryService.markAllRead(authentication);
		return ResponseEntity.noContent().build();
	}
}
