package datasa.repository;


import domain.entity.Trip;
import domain.entity.TripLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripLocationRepository extends JpaRepository<TripLocation, Long> {

    List<TripLocation> findByTripOrderByOrderNoAsc(Trip trip);
}
