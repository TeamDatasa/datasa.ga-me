package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
		name = "trip_cancel_request",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_cancel_request_application", columnNames = "application_id")
		},
		indexes = {
				@Index(name = "idx_cancel_request_status", columnList = "status")
		}
)
@Getter
@NoArgsConstructor
public class TripCancel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cancel_request_id")
	private Long cancelRequestId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id", nullable = false)
	private Application application;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private Status status = Status.PENDING;
	
	@Column(name = "requested_at", nullable = false)
	private LocalDateTime requestedAt;
	
	@Column(name = "decided_at")
	private LocalDateTime decidedAt;
	
	@PrePersist
	void onCreate() {
		this.requestedAt = LocalDateTime.now();
		if (this.status == null) this.status = Status.PENDING;
	}
	
	public static TripCancel pending(Application app) {
		TripCancel r = new TripCancel();
		r.application = app;
		r.status = Status.PENDING;
		return r;
	}
	
	public void approve() {
		this.status = Status.APPROVED;
		this.decidedAt = LocalDateTime.now();
	}
	
	public void reject() {
		this.status = Status.REJECTED;
		this.decidedAt = LocalDateTime.now();
	}
	
	public enum Status {
		PENDING, APPROVED, REJECTED
	}
}