package datasa.repository;




import datasa.entity.Trip;
import datasa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByHostUser(User hostUser);

    List<Trip> findByStatus(Trip.Status status);
}
