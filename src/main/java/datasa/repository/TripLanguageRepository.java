package datasa.repository;


import datasa.domain.entity.Trip;
import datasa.domain.entity.TripLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripLanguageRepository extends JpaRepository<TripLanguage, Long> {

    List<TripLanguage> findByTrip(Trip trip);
}
