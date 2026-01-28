package datasa.repository;


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
}
