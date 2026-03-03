package datasa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomListDto {

    private Long roomId;
    private Long tripId;
    private String title;
    private String theme;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
}
