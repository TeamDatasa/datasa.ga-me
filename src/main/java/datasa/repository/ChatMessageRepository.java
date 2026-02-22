package datasa.repository;


import datasa.domain.entity.ChatMessage;
import datasa.domain.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {




    Page<ChatMessage> findByChatRoom_RoomIdOrderByCreatedAtDesc(
            Long roomId,
            Pageable pageable
    );

    //  마지막 메시지
    Optional<ChatMessage> findTopByChatRoom_RoomIdOrderByCreatedAtDesc(Long roomId);

    // 안 읽은 메시지 수
    @Query("""
    select count(m)
    from ChatMessage m
    where m.chatRoom.roomId = :roomId
      and m.sender.userId <> :userId
      and ( :readAt is null or m.createdAt > :readAt )
""")
    long countUnreadMessages(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId,
            @Param("readAt") LocalDateTime readAt
    );

    long countByChatRoom_RoomIdAndSender_UserIdNot(Long roomId, Long userId);
}
