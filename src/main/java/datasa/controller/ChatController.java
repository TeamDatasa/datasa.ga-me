package datasa.controller;

import datasa.domain.dto.ChatMessageRequestDto;
import datasa.domain.dto.ChatMessageResponseDto;
import datasa.domain.dto.ChatRoomListDto;
import datasa.domain.dto.ChatRoomResponseDto;
import datasa.domain.entity.ChatMember;
import datasa.domain.entity.ChatRoom;
import datasa.domain.entity.User;
import datasa.repository.ChatMemberRepository;
import datasa.repository.ChatRoomRepository;
import datasa.repository.UserRepository;
import datasa.service.ChatRoomService;
import datasa.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
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

    @GetMapping("/chat/room/{tripId}")
    public String chatRoom(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // 1️⃣ 로그인 유저
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Long userId = user.getUserId();

        // 2️⃣ 채팅방 찾기
        ChatRoom room = chatRoomRepository.findByTrip_TripId(tripId)
                .orElseThrow(() -> new IllegalStateException("채팅방 없음"));

        Long roomId = room.getRoomId();

        // 3️⃣ ChatMember 검증 (이미 있는 레포 사용)
        ChatMember member = chatMemberRepository
                .findByChatRoom_RoomIdAndUser_UserId(roomId, userId)
                .orElseThrow(() -> new IllegalStateException("채팅방 멤버 아님"));

        if (!member.isActive()) {
            throw new AccessDeniedException("채팅방에서 나간 사용자");
        }


        System.out.println("======================================");
        System.out.println("[CHAT ROOM ENTER]");
        System.out.println("tripId = " + tripId);
        System.out.println("email  = " + email);
        System.out.println("userId = " + userId);

        // 4️⃣ 화면 전달
        model.addAttribute("roomId", roomId);
        model.addAttribute("userId", userId);

        return "chat/chat-room";
    }



    @GetMapping("/chat/rooms")
    public String chatRooms(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        List<ChatRoom> rooms =
                chatMemberRepository.findActiveChatRoomsByUserId(user.getUserId());

        String email = userDetails.getUsername();
        User user1 = userRepository.findByEmail(email)
                .orElseThrow();
        Long userId = user1.getUserId();
        System.out.println("======================================");
        System.out.println("[CHAT ROOMS]");
        System.out.println("email   = " + email);
        System.out.println("userId  = " + userId);
        System.out.println("======================================");
        model.addAttribute("rooms", rooms);
        model.addAttribute("userId", user.getUserId());

        return "chat/chat-room-list";
    }



}
