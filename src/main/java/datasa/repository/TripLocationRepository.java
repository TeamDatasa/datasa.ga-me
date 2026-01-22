package datasa.repository;


import datasa.domain.entity.Trip;
import datasa.domain.entity.TripLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripLocationRepository extends JpaRepository<TripLocation, Long> {

    List<TripLocation> findByTripOrderByOrderNoAsc(Trip trip);
}
