package datasa.service;

import datasa.domain.dto.NotificationResponse;
import datasa.domain.entity.Notification;
import datasa.domain.entity.User;
import datasa.repository.NotificationRepository;
import datasa.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {
	
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final NotificationService notificationService;
	
	
	@PersistenceContext
	private EntityManager em;
	
	private static final Long DEFAULT_USER_ID = 1L;
	private static final String ANONYMOUS = "anonymousUser";
	
	@Transactional(readOnly = true)
	public long unreadCount(Authentication authentication) {
		User me = currentUserOrDefault(authentication);
		return notificationRepository.countByUserAndIsReadFalse(me);
	}
	
	@Transactional(readOnly = true)
	public List<NotificationResponse> recent(Authentication authentication, int limit) {
		User me = currentUserOrDefault(authentication);
		return notificationRepository.findByUserOrderByCreatedAtDesc(me)
				.stream()
				.limit(Math.max(1, Math.min(limit, 50)))
				.map(NotificationResponse::from)
				.toList();
	}
	
	@Transactional
	public void markAllRead(Authentication authentication) {
		User me = currentUserOrDefault(authentication);
		List<Notification> unread = notificationRepository.findByUserAndIsReadFalse(me);
		unread.forEach(n -> n.setIsRead(true));
	}
	
	private User currentUserOrDefault(Authentication authentication) {
		// 무로그인/익명은 user_id=1을 기본 유저로 사용
		if (authentication == null || authentication.getPrincipal() == null) {
			ensureDefaultUserExists();
			return userRepository.findById(DEFAULT_USER_ID).orElseThrow();
		}
		
		Object principalObj = authentication.getPrincipal();
		
		if (principalObj instanceof String principalStr) {
			if (ANONYMOUS.equals(principalStr)) {
				ensureDefaultUserExists();
				return userRepository.findById(DEFAULT_USER_ID).orElseThrow();
			}
			
			// principal이 이메일 문자열인 경우
			return userRepository.findByEmail(principalStr)
					.orElseGet(() -> {
						ensureDefaultUserExists();
						return userRepository.findById(DEFAULT_USER_ID).orElseThrow();
					});
		}
		
		// ✅ principal이 UserDetails인 경우(현재 JwtAuthFilter가 이 케이스)
		if (principalObj instanceof UserDetails userDetails) {
			String email = userDetails.getUsername(); // 보통 username에 email이 들어감
			return userRepository.findByEmail(email)
					.orElseGet(() -> {
						ensureDefaultUserExists();
						return userRepository.findById(DEFAULT_USER_ID).orElseThrow();
					});
		}
		
		// 기타 타입은 toString으로 fallback
		String principal = principalObj.toString();
		if (ANONYMOUS.equals(principal)) {
			ensureDefaultUserExists();
			return userRepository.findById(DEFAULT_USER_ID).orElseThrow();
		}
		
		return userRepository.findByEmail(principal)
				.orElseGet(() -> {
					ensureDefaultUserExists();
					return userRepository.findById(DEFAULT_USER_ID).orElseThrow();
				});
	}
	
	private void ensureDefaultUserExists() {
		if (userRepository.existsById(DEFAULT_USER_ID)) return;
		
		em.createNativeQuery("""
            INSERT INTO user
            (user_id, email, password_hash, name, role, local_verified, status, created_at, updated_at, email_verified)
            VALUES
            (1, 'host1@local.test', 'TEST_HASH', '호스트1', 'USER', 0, 'ACTIVE', NOW(), NOW(), 1)
        """).executeUpdate();
		
		em.flush();
	}
	
	
	// 알림 삭제(단건)
	@Transactional
	public void deleteOne(Authentication authentication, Long notificationId) {
		User me = currentUserOrDefault(authentication);
		
		Notification n = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
		
		// 내 알림인지 검증
		if (!n.getUser().getUserId().equals(me.getUserId())) {
			throw new org.springframework.security.access.AccessDeniedException("삭제 권한 없음");
		}
		
		notificationRepository.delete(n);
	}
	
	// 알림 읽음 처리(단건)
	@Transactional
	public void markOneRead(Authentication authentication, Long notificationId) {
		User me = currentUserOrDefault(authentication);
		
		Notification n = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
		
		// 내 알림인지 검증
		if (!n.getUser().getUserId().equals(me.getUserId())) {
			throw new org.springframework.security.access.AccessDeniedException("읽음 처리 권한 없음");
		}
		
		if (!Boolean.TRUE.equals(n.getIsRead())) {
			n.setIsRead(true);
		}
	}
	
}
