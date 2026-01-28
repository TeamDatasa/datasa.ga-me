package datasa.domain.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "mbti", length = 4)
    private String mbti;

    @Column(name = "smoking")
    private Boolean smoking;

    @Column(name = "drinking")
    private Boolean drinking;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @Column(name = "local_verified", nullable = false)
    private Boolean localVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
	
	@Column(name = "email_verified", nullable = false)
	private Boolean emailVerified = false;
	
	@PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }



    /* ===== ENUM ===== */

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum Role {
        USER, HOST
    }

    public enum Status {
        ACTIVE, INACTIVE, SUSPENDED
    }
	
	public static User create(String email, String passwordHash, String name, Role role) {
		User user = new User();
		user.email = email;
		user.passwordHash = passwordHash;
		user.name = name;
		user.role = role;
		return user;
	}
	
	public void deactivate() {
		this.status = Status.INACTIVE;
	}
	
	public void updateProfile(
			String name,
			LocalDate birthDate,
			Gender gender,
			String countryCode,
			String region,
			String mbti,
			Boolean smoking,
			Boolean drinking,
			String bio
	) {
		this.name = name;
		this.birthDate = birthDate;
		this.gender = gender;
		this.countryCode = countryCode;
		this.region = region;
		this.mbti = mbti;
		this.smoking = smoking;
		this.drinking = drinking;
		this.bio = bio;
	}
	
	// 비번 변경 메서드
	public void changePassword(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	// 인증 처리 메서드
	public Boolean getEmailVerified() { return emailVerified; }
	
	public void verifyEmail() {
		this.emailVerified = true;
	}
	
	// Role 변경
	public void changeRole(Role role) {
		this.role = role;
	}
	
	
	
}
