package datasa.service;

import datasa.domain.dto.NotificationResponse;
import datasa.domain.entity.Notification;
import datasa.domain.entity.User;
import datasa.repository.NotificationRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
	
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	
	@Transactional(readOnly = true)
	public long unreadCount(Authentication authentication) {
		User me = currentUser(authentication);
		return notificationRepository.countByUserAndIsReadFalse(me);
	}
	
	@Transactional(readOnly = true)
	public List<NotificationResponse> recent(Authentication authentication, int limit) {
		User me = currentUser(authentication);
		return notificationRepository.findByUserOrderByCreatedAtDesc(me)
				.stream()
				.limit(Math.max(1, Math.min(limit, 50)))
				.map(NotificationResponse::from)
				.toList();
	}
	
	@Transactional
	public void markAllRead(Authentication authentication) {
		User me = currentUser(authentication);
		List<Notification> unread = notificationRepository.findByUserAndIsReadFalse(me);
		unread.forEach(n -> n.setIsRead(true)); // dirty checking
	}
	
	@Transactional
	public NotificationResponse createTestCommentNotification(Authentication authentication) {
		User me = currentUser(authentication);
		
		// 테스트: "누군가 내 게시글에 댓글" 시나리오
		long actorUserId = 999L;
		long fakeTripId = 0L;
		long fakeCommentId = System.currentTimeMillis() % 1_000_000;
		
		Notification n = Notification.tripComment(me.getUserId(), actorUserId, fakeTripId, fakeCommentId);
		Notification saved = notificationRepository.save(n);
		return NotificationResponse.from(saved);
	}
	
	private User currentUser(Authentication authentication) {
		if (authentication == null || authentication.getPrincipal() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
		}
		String email = authentication.getPrincipal().toString(); // JwtTokenProvider가 principal=email로 넣고 있음
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
	}
}
