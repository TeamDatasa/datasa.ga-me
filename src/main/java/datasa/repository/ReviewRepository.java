package datasa.repository;

import datasa.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	
	boolean existsByUser_UserIdAndTrip_TripId(Long userId, Long tripId);
	
	Optional<Review> findByUser_UserIdAndTrip_TripId(Long userId, Long tripId);
	
	List<Review> findAllByTrip_TripIdOrderByCreatedAtDesc(Long tripId);
	
	long countByTrip_TripId(Long tripId);
	
	@Query("select coalesce(avg(r.rating), 0) from Review r where r.trip.tripId = :tripId")
	double avgRatingByTripId(@Param("tripId") Long tripId);
	
	@Query("select count(r) from Review r where r.trip.hostUser.userId = :hostId")
	long countByHostId(@Param("hostId") Long hostId);
	
	@Query("select coalesce(avg(r.rating), 0) from Review r where r.trip.hostUser.userId = :hostId")
	double avgRatingByHostId(@Param("hostId") Long hostId);
}
