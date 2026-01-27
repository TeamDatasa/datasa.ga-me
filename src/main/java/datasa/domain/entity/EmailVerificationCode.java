package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_code")
@Getter
@NoArgsConstructor
public class EmailVerificationCode {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "email_verification_code_id")
	private Long id;
	
	@Column(name = "email", nullable = false, length = 255)
	private String email;
	
	@Column(name = "code", nullable = false, length = 6)
	private String code;
	
	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;
	
	@Column(name = "used", nullable = false)
	private Boolean used = false;
	
	public static EmailVerificationCode create(String email, String code, LocalDateTime expiresAt) {
		EmailVerificationCode e = new EmailVerificationCode();
		e.email = email;
		e.code = code;
		e.expiresAt = expiresAt;
		e.used = false;
		return e;
	}
	
	public boolean isExpired() {
		return expiresAt.isBefore(LocalDateTime.now());
	}
	
	public void markUsed() {
		this.used = true;
	}
}
