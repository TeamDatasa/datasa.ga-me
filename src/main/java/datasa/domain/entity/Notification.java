package datasa.domain.entity;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notification",
        indexes = {
                @Index(name = "idx_notification_user", columnList = "user_id"),
                @Index(name = "idx_notification_created", columnList = "created_at")
        }
)
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "ref_id")
    private Long refId; // trip_id, application_id 등

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "body", nullable = false, length = 255)
    private String body;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isRead == null) {
            this.isRead = false;
        }
    }
	
	public static Notification tripLike(Long ownerUserId, Long actorUserId, Long tripId) {
		Notification n = new Notification();
		n.user = userRef(ownerUserId);
		n.type = Type.LIKE;
		n.refId = tripId;
		n.title = "좋아요";
		n.body = "내 게시글에 좋아요가 달렸습니다. (userId=" + actorUserId + ")";
		n.isRead = false;
		return n;
	}
	
	public static Notification tripComment(Long ownerUserId, Long actorUserId, Long tripId, Long commentId) {
		Notification n = new Notification();
		n.user = userRef(ownerUserId);
		n.type = Type.COMMENT;
		n.refId = tripId; // refId는 tripId로 통일
		n.title = "댓글";
		n.body = "내 게시글에 댓글이 달렸습니다. (commentId=" + commentId + ", userId=" + actorUserId + ")";
		n.isRead = false;
		return n;
	}
	
	private static User userRef(Long userId) {
		User u = new User();
		u.setUserId(userId);
		return u;
	}

    /* ===== ENUM ===== */
    public enum Type {
        APPLY,        // 신청
        APPROVED,     // 승인
        REJECTED,     // 거절
        COMMENT,      // 댓글
        CHAT,         // 채팅
        SYSTEM,        // 시스템 알림
		LIKE
    }
	

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	
}
