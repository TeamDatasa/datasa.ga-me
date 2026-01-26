package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "password_reset_token",
		indexes = {
				@Index(name = "idx_prt_token", columnList = "token", unique = true),
				@Index(name = "idx_prt_user", columnList = "user_id")
		}
)
@Getter
@NoArgsConstructor
public class PasswordResetToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(nullable = false, unique = true, length = 100)
	private String token;
	
	@Column(nullable = false)
	private LocalDateTime expiresAt;
	
	@Column(nullable = false)
	private boolean used = false;
	
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@PrePersist
	void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
	
	public static PasswordResetToken create(User user, String token, LocalDateTime expiresAt) {
		PasswordResetToken t = new PasswordResetToken();
		t.user = user;
		t.token = token;
		t.expiresAt = expiresAt;
		t.used = false;
		return t;
	}
	
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}
	
	public void markUsed() {
		this.used = true;
	}
}
