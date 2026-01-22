package datasa.repository;


import datasa.entity.Trip;
import datasa.entity.TripLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripLocationRepository extends JpaRepository<TripLocation, Long> {

    List<TripLocation> findByTripOrderByOrderNoAsc(Trip trip);
}
