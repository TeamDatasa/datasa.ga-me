package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
		name = "REVIEW",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_review_user_trip", columnNames = {"user_id", "trip_id"})
		},
		indexes = {
				@Index(name = "idx_review_user", columnList = "user_id"),
				@Index(name = "idx_review_trip", columnList = "trip_id")
		}
)
@Getter
@Setter
@NoArgsConstructor
public class Review {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long reviewId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id", nullable = false)
	private Trip trip;
	
	@Column(name = "rating", nullable = false)
	private int rating;
	
	@Column(name = "content", nullable = false, length = 2000)
	private String content;
	
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	public static Review create(User user, Trip trip, int rating, String content) {
		Review r = new Review();
		r.user = user;
		r.trip = trip;
		r.rating = rating;
		r.content = content;
		r.createdAt = LocalDateTime.now();
		return r;
	}
	
	public void update(int rating, String content) {
		this.rating = rating;
		this.content = content;
		this.updatedAt = LocalDateTime.now();
	}
}
