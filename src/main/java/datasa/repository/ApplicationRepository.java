package datasa.repository;


import datasa.entity.Application;
import datasa.entity.Trip;
import datasa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByUser(User user);

    List<Application> findByTrip(Trip trip);

    Optional<Application> findByTripAndUser(Trip trip, User user);

    long countByTripAndStatus(Trip trip, Application.Status status);
}
