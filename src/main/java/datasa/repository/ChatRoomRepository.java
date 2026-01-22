package datasa.repository;


import domain.entity.ChatRoom;
import domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByTrip(Trip trip);
}
