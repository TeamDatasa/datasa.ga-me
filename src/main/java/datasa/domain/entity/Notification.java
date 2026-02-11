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
	
	public static Notification tripLike(User owner, String actorName, String tripTitle, Long tripId) {
		Notification n = new Notification();
		n.user = owner;
		n.type = Type.LIKE;
		n.refId = tripId;
		n.title = "좋아요";
		
		String safeActor = actorName == null ? "누군가" : actorName.trim();
		String safeTitle = tripTitle == null ? "내 게시글" : tripTitle.trim();
		
		// ✅ 너무 길면 제목만 먼저 줄이기 (여유 있게)
		if (safeTitle.length() > 60) {
			safeTitle = safeTitle.substring(0, 60) + "...";
		}
		if (safeActor.length() > 30) {
			safeActor = safeActor.substring(0, 30) + "...";
		}
		
		String body = safeActor + "님이 \"" + safeTitle + "\" 게시글에 좋아요를 눌렀습니다.";
		
		// ✅ 최종적으로 body 255 초과 방지
		if (body.length() > 255) {
			body = body.substring(0, 252) + "...";
		}
		
		n.body = body;
		n.isRead = false;
		return n;
	}
	
	public static Notification tripComment(User owner, Long actorUserId, Long tripId, Long commentId) {
		Notification n = new Notification();
		n.user = owner;
		n.type = Type.COMMENT;
		n.refId = tripId; // refId는 tripId로 통일
		n.title = "댓글";
		n.body = "내 게시글에 댓글이 달렸습니다. (commentId=" + commentId + ", userId=" + actorUserId + ")";
		n.isRead = false;
		return n;
	}
	
	public enum Type {
		APPLY,        // 신청
		APPROVED,     // 승인
		REJECTED,     // 거절
		COMMENT,      // 댓글
		CHAT,         // 채팅
		SYSTEM,       // 시스템 알림
		LIKE
	}
	
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	
	// 신청 알림
	public static Notification tripApply(User owner, String actorName, String tripTitle, Long tripId, Long applicationId) {
		Notification n = new Notification();
		n.user = owner;
		n.type = Type.APPLY;
		n.refId = tripId;
		n.title = "여정 신청";

		String safeActor = (actorName == null || actorName.isBlank()) ? "누군가" : actorName.trim();
		String safeTitle = (tripTitle == null || tripTitle.isBlank()) ? "여정" : tripTitle.trim();

		if (safeActor.length() > 30) safeActor = safeActor.substring(0, 30) + "...";
		if (safeTitle.length() > 60) safeTitle = safeTitle.substring(0, 60) + "...";

		String body = safeActor + "님이 [" + safeTitle + "] 여정에 참여신청을 했습니다.";

		if (body.length() > 255) {
			body = body.substring(0, 252) + "...";
		}

		n.body = body;
		n.isRead = false;
		return n;
	}

	// 신청 결과(승인) 알림
	public static Notification tripApplicationApproved(User owner, String tripTitle, Long tripId) {
		Notification n = new Notification();
		n.user = owner;
		n.type = Type.APPROVED;
		n.refId = tripId;
		n.title = "여정 신청 결과";

		String safeTitle = (tripTitle == null || tripTitle.isBlank()) ? "여정" : tripTitle.trim();
		if (safeTitle.length() > 60) safeTitle = safeTitle.substring(0, 60) + "...";

		String body = "[" + safeTitle + "]여정 신청 결과 : 수락되었습니다.";
		if (body.length() > 255) body = body.substring(0, 252) + "...";

		n.body = body;
		n.isRead = false;
		return n;
	}

	// 신청 결과(거절) 알림
	public static Notification tripApplicationRejected(User owner, String tripTitle, Long tripId) {
		Notification n = new Notification();
		n.user = owner;
		n.type = Type.REJECTED;
		n.refId = tripId;
		n.title = "여정 신청 결과";

		String safeTitle = (tripTitle == null || tripTitle.isBlank()) ? "여정" : tripTitle.trim();
		if (safeTitle.length() > 60) safeTitle = safeTitle.substring(0, 60) + "...";

		String body = "[" + safeTitle + "]여정 신청 결과 : 거절되었습니다.";
		if (body.length() > 255) body = body.substring(0, 252) + "...";

		n.body = body;
		n.isRead = false;
		return n;
	}

	public static Notification tripCommentCreated(User owner, String actorName, String tripTitle, Long tripId, Long commentId) {
		Notification n = new Notification();
		n.user = owner;
		n.type = Type.COMMENT;
		n.refId = tripId;
		n.title = "댓글";

		String safeActor = (actorName == null || actorName.isBlank()) ? "누군가" : actorName.trim();
		String safeTitle = (tripTitle == null || tripTitle.isBlank()) ? "내 게시글" : tripTitle.trim();

		if (safeTitle.length() > 60) safeTitle = safeTitle.substring(0, 60) + "...";
		if (safeActor.length() > 30) safeActor = safeActor.substring(0, 30) + "...";

		String body = safeActor + "님이 \"" + safeTitle + "\" 여정에 댓글을 달았습니다.";
		if (body.length() > 255) body = body.substring(0, 252) + "...";

		n.body = body;
		n.isRead = false;
		return n;
	}
}
