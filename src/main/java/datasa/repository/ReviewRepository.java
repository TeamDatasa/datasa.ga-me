package datasa.repository;

import datasa.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	
	Optional<Review> findByUser_UserIdAndTrip_TripId(Long userId, Long tripId);
	
	long countByUser_UserIdAndTrip_TripId(Long userId, Long tripId);
}
