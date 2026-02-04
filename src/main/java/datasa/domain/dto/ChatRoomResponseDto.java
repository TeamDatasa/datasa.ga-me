package datasa.domain.dto;

import datasa.domain.entity.ChatRoom;
import datasa.domain.entity.Trip;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomResponseDto {

    private Long roomId;
    private String title;
    private String lastMessage;
    private String lastMessageTime;
    private int unreadCount;
}


