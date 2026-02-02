package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripLike {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "like_id")
	private Long likeId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id", nullable = false)
	private Trip trip;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
	
	private TripLike(Trip trip, User user) {
		this.trip = trip;
		this.user = user;
	}
	
	public static TripLike of(Trip trip, User user) {
		return new TripLike(trip, user);
	}
}