package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long commentId;
	
	// 게시글 ID (trip_id)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id", nullable = false)
	private Trip trip;
	
	// 작성자 ID (user_id)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	// 부모댓글 ID (parent_comment_id) - 대댓글이면 값 존재
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_comment_id")
	private Comment parent;
	
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;
	
	// 상태(status) - 기본 ACTIVE
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status = Status.ACTIVE;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	public Comment(Trip trip, User user, Comment parent, String content) {
		this.trip = trip;
		this.user = user;
		this.parent = parent;
		this.content = content;
	}
	
	public void updateContent(String content) {
		this.content = content;
	}
	
	// 본 댓글이 삭제되어도 대댓글은 살려둠
	public void markDeletedKeepingReplies() {
		this.status = Status.DELETED;
		this.content = "삭제된 댓글입니다.";
	}
	
	public boolean isActive() {
		return this.status == Status.ACTIVE;
	}
	
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
	
	public enum Status {
		ACTIVE, HIDDEN, DELETED
	}
}
