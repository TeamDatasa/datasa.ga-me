package datasa.repository;


import domain.entity.Trip;
import domain.entity.TripLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripLanguageRepository extends JpaRepository<TripLanguage, Long> {

    List<TripLanguage> findByTrip(Trip trip);
}
