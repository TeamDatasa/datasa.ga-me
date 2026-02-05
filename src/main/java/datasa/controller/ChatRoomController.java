package datasa.controller;

import datasa.domain.dto.ChatRoomListDto;
import datasa.domain.dto.ChatRoomResponseDto;
import datasa.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     *  채팅방 목록 API
     * /chat/api/rooms?userId=1
     */
    @ResponseBody
    @GetMapping("")
    public List<ChatRoomListDto> getMyChatRooms(
            @RequestParam Long userId
    ) {
        return chatRoomService.getMyChatRooms(userId);
    }

    @PostMapping("/rooms/{roomId}/read")
    public void readChatRoom(
            @PathVariable Long roomId,
            @RequestParam Long userId
    ) {
        chatRoomService.markAsRead(roomId, userId);
    }

}
