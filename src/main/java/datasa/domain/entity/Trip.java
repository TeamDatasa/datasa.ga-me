package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip")
@Data
@NoArgsConstructor
public class Trip {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trip_id")
	private Long tripId;
	
	/**
	 * 가이드(호스트)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "host_user_id", nullable = false)
	private User hostUser;
	
	@Column(name = "title", nullable = false, length = 120)
	private String title;
	
	@Column(name = "description", nullable = false, columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "region", nullable = false)
	private String region;
	
	@Column(name = "estimated_cost")
	private Integer estimatedCost; // KRW

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "edit_lock_days", nullable = false)
    private Integer editLockDays = 7;

    @Column(name = "theme", nullable = false)
    private String theme;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.DRAFT;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
	public enum Status {
		DRAFT, OPEN, CLOSED
	}
}
