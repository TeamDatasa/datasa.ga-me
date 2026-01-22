package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
		name = "comment_like",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_comment_like_comment_user", columnNames = {"comment_id", "user_id"})
		}
)
@Getter
@NoArgsConstructor
public class CommentLike {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_like_id")
	private Long commentLikeId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", nullable = false)
	private Comment comment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	public CommentLike(Comment comment, User user) {
		this.comment = comment;
		this.user = user;
	}
	
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}
