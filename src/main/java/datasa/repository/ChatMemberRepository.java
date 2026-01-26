package datasa.repository;


import datasa.domain.entity.ChatMember;
import datasa.domain.entity.ChatRoom;
import datasa.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    // 채팅방 전체 멤버
    List<ChatMember> findByChatRoom(ChatRoom chatRoom);

    // 승인 시 중복 참여 방지
    Optional<ChatMember> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    // 채팅 접근 권한 체크 (활성 멤버만)
    Optional<ChatMember> findByChatRoomAndUserAndLeftAtIsNull(
            ChatRoom chatRoom,
            User user
    );
}




