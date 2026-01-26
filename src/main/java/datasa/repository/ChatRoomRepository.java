package datasa.repository;


import datasa.domain.entity.ChatRoom;
import datasa.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByTrip(Trip trip);

    Optional<ChatRoom> findByTrip_TripId(Long tripId);

}
