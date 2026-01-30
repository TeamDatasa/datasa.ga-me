package datasa.service;

import datasa.domain.dto.CommentCreateRequest;
import datasa.domain.dto.CommentResponse;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.TripRepository;
import datasa.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationTestService {
	
	private final CommentService commentService;
	private final TripRepository tripRepository;
	private final UserRepository userRepository;
	
	@PersistenceContext
	private final EntityManager em;
	
	// test : 로그인 없이 강제 테스트
	@Transactional
	public CommentResponse forceCommentNotificationNoAuth() {
		Long hostUserId = 1L;
		ensureUserExists(hostUserId, "host1@local.test", "호스트1");
		ensureUserExists(3L, "user3@local.test", "테스트유저3");
		
		ensureTrip2ExistsAndForceHost(hostUserId);
		
		CommentCreateRequest req = new CommentCreateRequest(
				3L,
				null,
				"테스트용 댓글입니다."
		);
		
		return commentService.create(2L, req);
	}
	
	private void ensureUserExists(Long userId, String email, String name) {
		if (userRepository.existsById(userId)) return;
		
		em.createNativeQuery("""
        INSERT INTO user
        (user_id, email, password_hash, name, role, local_verified, status, created_at, updated_at, email_verified)
        VALUES
        (:id, :email, 'TEST_HASH', :name, 'USER', 0, 'ACTIVE', NOW(), NOW(), 1)
    """)
				.setParameter("id", userId)
				.setParameter("email", email)
				.setParameter("name", name)
				.executeUpdate();
	}

	
	private void ensureUser3Exists() {
		if (userRepository.existsById(3L)) return;
		
		// MySQL은 IDENTITY 컬럼이라도 명시적으로 user_id 넣어 INSERT 가능
		em.createNativeQuery("""
            INSERT INTO user
            (user_id, email, password_hash, name, role, local_verified, status, created_at, updated_at, email_verified)
            VALUES
            (3, 'user3@local.test', 'TEST_HASH', '테스트유저3', 'USER', 0, 'ACTIVE', NOW(), NOW(), 1)
        """).executeUpdate();
	}
	
	private void ensureTrip2ExistsAndForceHost(Long hostUserId) {
		Trip trip = tripRepository.findById(2L).orElse(null);
		
		if (trip == null) {
			// trip_id=2로 강제 생성 (필수 컬럼 채움)
			em.createNativeQuery("""
                INSERT INTO trip
                (trip_id, host_user_id, title, description, region, estimated_cost, max_participants, duration_minutes,
                 start_at, end_at, edit_lock_days, theme, status, created_at, updated_at)
                VALUES
                (2, :hostUserId, '테스트용 여행글(2)', '테스트용 게시글입니다.', 'TEST', 0, 5, 60,
                 NOW(), DATE_ADD(NOW(), INTERVAL 1 HOUR), 7, 'TEST', 'OPEN', NOW(), NOW())
            """)
					.setParameter("hostUserId", hostUserId)
					.executeUpdate();
			
			em.flush();
			return;
		}
		
		// 존재하면 host를 현재 로그인 사용자로 강제 변경
		trip.setHostUser(userRef(hostUserId));
		tripRepository.save(trip);
	}
	
	private User userRef(Long userId) {
		User u = new User();
		u.setUserId(userId);
		return u;
	}
	
	private User currentUser(Authentication authentication) {
		if (authentication == null || authentication.getPrincipal() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
		}
		String email = authentication.getPrincipal().toString();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
	}
}
