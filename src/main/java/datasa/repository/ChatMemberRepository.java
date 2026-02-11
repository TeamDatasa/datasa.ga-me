package datasa.repository;

import datasa.domain.entity.ChatMember;
import datasa.domain.entity.ChatRoom;
import datasa.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    List<ChatMember> findByChatRoom(ChatRoom chatRoom);

    Optional<ChatMember> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    Optional<ChatMember> findByChatRoomAndUserAndLeftAtIsNull(ChatRoom chatRoom, User user);

    boolean existsByChatRoomAndUserAndLeftAtIsNull(ChatRoom chatRoom, User user);

    List<ChatMember> findByChatRoomAndLeftAtIsNull(ChatRoom chatRoom);

    Optional<ChatMember> findByChatRoom_RoomIdAndUser_UserId(Long roomId, Long userId);

    @Query("""
select cm.chatRoom
from ChatMember cm
where cm.user.userId = :userId
  and cm.leftAt is null
order by cm.joinedAt desc
""")
    List<ChatRoom> findActiveChatRoomsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from ChatMember cm where cm.chatRoom.roomId = :roomId")
    void deleteByChatRoom_RoomId(@Param("roomId") Long roomId);
}
