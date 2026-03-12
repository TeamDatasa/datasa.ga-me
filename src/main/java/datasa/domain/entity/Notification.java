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

    public enum Type {
        APPLY,
        APPROVED,
        REJECTED,
        COMMENT,
        CHAT,
        SYSTEM,
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
        n.title = "ツアー申請";

        String safeActor = (actorName == null || actorName.isBlank()) ? "誰か" : actorName.trim();
        String safeTitle = (tripTitle == null || tripTitle.isBlank()) ? "ツアー" : tripTitle.trim();

        if (safeActor.length() > 30) safeActor = safeActor.substring(0, 30) + "...";
        if (safeTitle.length() > 60) safeTitle = safeTitle.substring(0, 60) + "...";

        String body = safeActor + "さんが「" + safeTitle + "」ツアーに参加申請しました。";
        if (body.length() > 255) body = body.substring(0, 252) + "...";

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
        n.title = "申請結果";

        String safeTitle = (tripTitle == null || tripTitle.isBlank()) ? "ツアー" : tripTitle.trim();
        if (safeTitle.length() > 60) safeTitle = safeTitle.substring(0, 60) + "...";

        String body = "「" + safeTitle + "」ツアーの参加申請が承認されました。";
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
        n.title = "申請結果";

        String safeTitle = (tripTitle == null || tripTitle.isBlank()) ? "ツアー" : tripTitle.trim();
        if (safeTitle.length() > 60) safeTitle = safeTitle.substring(0, 60) + "...";

        String body = "「" + safeTitle + "」ツアーの参加申請が拒否されました。";
        if (body.length() > 255) body = body.substring(0, 252) + "...";

        n.body = body;
        n.isRead = false;
        return n;
    }

    // 댓글 알림
    public static Notification tripComment(User owner, Long actorUserId, Long tripId, Long commentId) {
        Notification n = new Notification();
        n.user = owner;
        n.type = Type.COMMENT;
        n.refId = tripId;
        n.title = "コメント";
        n.body = "自分の投稿にコメントが付きました。(commentId=" + commentId + ", userId=" + actorUserId + ")";
        n.isRead = false;
        return n;
    }

    public static Notification tripLike(User owner, String actorName, String tripTitle, Long tripId) {
        Notification n = new Notification();
        n.user = owner;
        n.type = Type.LIKE;
        n.refId = tripId;
        n.title = "いいね";

        String safeActor = (actorName == null || actorName.isBlank()) ? "誰か" : actorName.trim();
        String safeTitle = (tripTitle == null || tripTitle.isBlank()) ? "自分の投稿" : tripTitle.trim();

        if (safeTitle.length() > 60) safeTitle = safeTitle.substring(0, 60) + "...";
        if (safeActor.length() > 30) safeActor = safeActor.substring(0, 30) + "...";

        String body = safeActor + "さんが「" + safeTitle + "」にいいねしました。";
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
        n.title = "コメント";

        String safeActor = (actorName == null || actorName.isBlank()) ? "誰か" : actorName.trim();
        String safeTitle = (tripTitle == null || tripTitle.isBlank()) ? "自分の投稿" : tripTitle.trim();

        if (safeTitle.length() > 60) safeTitle = safeTitle.substring(0, 60) + "...";
        if (safeActor.length() > 30) safeActor = safeActor.substring(0, 30) + "...";

        String body = safeActor + "さんが「" + safeTitle + "」にコメントしました。";
        if (body.length() > 255) body = body.substring(0, 252) + "...";

        n.body = body;
        n.isRead = false;
        return n;
    }

    // 채팅방 개설 알림
    public static Notification tripChatRoomCreated(User owner, String tripTitle, Long tripId) {
        Notification n = new Notification();
        n.user = owner;
        n.type = Type.CHAT;
        n.refId = tripId;
        n.title = "チャット";

        String safeTitle = (tripTitle == null || tripTitle.isBlank()) ? "ツアー" : tripTitle.trim();
        if (safeTitle.length() > 60) safeTitle = safeTitle.substring(0, 60) + "...";

        String body = "「" + safeTitle + "」ツアーのチャットルームが作成されました。";
        if (body.length() > 255) body = body.substring(0, 252) + "...";

        n.body = body;
        n.isRead = false;
        return n;
    }
}