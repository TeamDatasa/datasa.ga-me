package datasa.repository;


import org.springframework.beans.factory.parsing.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByGooglePlaceId(String googlePlaceId);
}
