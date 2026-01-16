package datasa.repository;


import datasa.entity.ChatMember;
import datasa.entity.ChatRoom;
import datasa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    List<ChatMember> findByChatRoom(ChatRoom chatRoom);

    Optional<ChatMember> findByChatRoomAndUser(ChatRoom chatRoom, User user);
}
