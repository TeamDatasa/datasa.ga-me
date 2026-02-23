package datasa.repository;

import datasa.domain.entity.Trip;
import datasa.domain.entity.TripLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripLanguageRepository extends JpaRepository<TripLanguage, Long> {

    List<TripLanguage> findByTrip_TripId(Long tripId);

    List<TripLanguage> findByTrip(Trip trip);

    @Modifying
    @Query("delete from TripLanguage tl where tl.trip.tripId = :tripId")
    void deleteByTripId(@Param("tripId") Long tripId);
    
    @Query("""
    select tl.trip.tripId, tl.languageCode
    from TripLanguage tl
    where tl.trip.tripId in :tripIds
""")
    List<Object[]> findCodesByTripIds(@Param("tripIds") List<Long> tripIds);
}
