package datasa.controller;

import datasa.domain.dto.ChatMessageRequestDto;
import datasa.domain.dto.ChatMessageResponseDto;
import datasa.domain.dto.ChatRoomListDto;
import datasa.domain.dto.ChatRoomResponseDto;
import datasa.service.ChatRoomService;
import datasa.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트 SEND:
     *   /app/chat.send/{roomId}
     *
     * 서버 BROADCAST:
     *   /topic/chat/{roomId}
     */
    @MessageMapping("/chat.send/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequestDto dto,
            StompHeaderAccessor accessor
    ) {
        Long userId = getUserIdFromSession(accessor);

        ChatMessageResponseDto saved =
                chatService.sendMessage(roomId, userId, dto);

        // 구독자들에게 실시간 전파
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, saved);
    }

    private Long getUserIdFromSession(StompHeaderAccessor accessor) {
        if (accessor.getSessionAttributes() == null) {
            throw new IllegalStateException("WebSocket 세션이 없습니다.");
        }
        Object userIdObj = accessor.getSessionAttributes().get("userId");
        if (userIdObj == null) {
            throw new IllegalStateException("인증 정보(userId)가 없습니다. JwtHandshakeInterceptor 등록/헤더 전달을 확인하세요.");
        }
        if (!(userIdObj instanceof Long)) {
            // 혹시 String으로 들어오는 경우 대비
            try {
                return Long.valueOf(String.valueOf(userIdObj));
            } catch (Exception e) {
                throw new IllegalStateException("userId 형식이 올바르지 않습니다: " + userIdObj);
            }
        }
        return (Long) userIdObj;
    }

    @GetMapping("chat/room/{tripId}")
    public String chatRoom(@PathVariable Long tripId, Model model) {
        Long roomId = chatService.getOrCreateRoomIdByTrip(tripId);

        model.addAttribute("tripId", tripId);
        model.addAttribute("roomId", roomId);
        return "/chat/chat-room";
    }


    @GetMapping("/chat/rooms")
    public String chatRoomList(
            @RequestParam Long userId,
            Model model
    ) {
        model.addAttribute("userId", userId);
        return "chat/chat-room-list";
    }


}
