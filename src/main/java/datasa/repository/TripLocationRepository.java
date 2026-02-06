package datasa.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import aj.org.objectweb.asm.commons.Remapper;
import datasa.domain.entity.Trip;
import datasa.domain.entity.TripLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripLocationRepository extends JpaRepository<TripLocation, Long> {

    List<TripLocation> findByTripOrderByOrderNoAsc(Trip trip);

    Remapper findFirstByTrip_TripId(Long tripId);
	
	
	List<TripLocation> findByTrip_TripIdOrderByOrderNoAsc(Long tripId);
	
	void deleteByTrip(Trip trip);
	
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("delete from TripLocation tl where tl.trip.tripId = :tripId")
	int deleteAllByTripId(@Param("tripId") Long tripId);
	
}
