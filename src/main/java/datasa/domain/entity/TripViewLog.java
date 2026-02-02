package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
		name = "trip_view_log",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_view_user_trip", columnNames = {"user_id", "trip_id"})
		},
		indexes = {
				@Index(name = "idx_view_user", columnList = "user_id"),
				@Index(name = "idx_view_user_time", columnList = "user_id, viewed_at")
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripViewLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "view_id")
	private Long viewId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id", nullable = false)
	private Trip trip;
	
	@Column(name = "viewed_at", nullable = false)
	private LocalDateTime viewedAt;
	
	private TripViewLog(User user, Trip trip) {
		this.user = user;
		this.trip = trip;
		this.viewedAt = LocalDateTime.now();
	}
	
	public static TripViewLog of(User user, Trip trip) {
		return new TripViewLog(user, trip);
	}
	
	public void touch() {
		this.viewedAt = LocalDateTime.now();
	}
	
	@PrePersist
	protected void onCreate() {
		if (this.viewedAt == null) this.viewedAt = LocalDateTime.now();
	}
}
