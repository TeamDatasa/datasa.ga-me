package datasa.repository;


import datasa.domain.entity.ChatMessage;
import datasa.domain.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);

    Page<ChatMessage> findByChatRoom_RoomIdOrderByCreatedAtAsc(
            Long roomId,
            Pageable pageable
    );

    Page<ChatMessage> findByChatRoom_RoomIdOrderByCreatedAtDesc(
            Long roomId,
            Pageable pageable
    );

}
