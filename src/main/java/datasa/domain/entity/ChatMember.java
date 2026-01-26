package datasa.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "CHAT_MEMBER",
        indexes = {
                @Index(name = "idx_chat_member_room", columnList = "room_id"),
                @Index(name = "idx_chat_member_user", columnList = "user_id")
        }
)
@Getter
@NoArgsConstructor
public class ChatMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_member_id")
    private Long chatMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @PrePersist
    protected void onJoin() {
        this.joinedAt = LocalDateTime.now();
    }

    /** 현재 채팅방 참여 중 여부 */
    public boolean isActive() {
        return leftAt == null;
    }

    public ChatMember(ChatRoom chatRoom, User user) {
        this.chatRoom = chatRoom;
        this.user = user;
    }

    public void leave() {
        this.leftAt = LocalDateTime.now();
    }
    @Column(name = "read_at")
    private LocalDateTime readAt;

    public void markAsRead() {
        this.readAt = LocalDateTime.now();
    }

}
