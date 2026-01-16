package datasa.repository;


import datasa.entity.Trip;
import datasa.entity.TripLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripLanguageRepository extends JpaRepository<TripLanguage, Long> {

    List<TripLanguage> findByTrip(Trip trip);
}
