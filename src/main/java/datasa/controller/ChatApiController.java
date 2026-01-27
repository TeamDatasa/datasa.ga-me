//package datasa.controller;
//
//import datasa.repository.ChatRoomRepository;
//import datasa.service.ChatService;
//import datasa.domain.entity.ChatMessage;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/chat")
//public class ChatApiController {
//
//    private final ChatService chatService;
//    private final ChatRoomRepository chatRoomRepository;
//
//    @GetMapping("/room-id")
//    public Long getRoomId(@RequestParam Long tripId) {
//        return chatRoomRepository.findByTrip_TripId(tripId)
//                .orElseThrow()
//                .getRoomId();
//    }
//
//
//    /* =========================
//       메시지 전송
//       POST /api/chat/rooms/{roomId}/messages
//    ========================= */
//    @PostMapping("/rooms/{roomId}/messages")
//    public void sendMessage(
//            @PathVariable Long roomId,
//            @RequestParam Long userId,
//            @RequestParam String message
//    ) {
//        chatService.sendMessage(roomId, userId, message);
//    }
//
//    /* =========================
//       메시지 조회 (페이징)
//       GET /api/chat/rooms/{roomId}/messages
//    ========================= */
//    @GetMapping("/rooms/{roomId}/messages")
//    public Page<ChatMessage> getMessages(
//            @PathVariable Long roomId,
//            @RequestParam Long userId,
//            Pageable pageable
//    ) {
//        return chatService.getMessages(roomId, userId, pageable);
//    }
//
//
//    /* =========================
//   메시지 번역 요청 (C_008)
//   POST /api/chat/rooms/{roomId}/messages/{messageId}/translate
//========================= */
//    @PostMapping("/rooms/{roomId}/messages/{messageId}/translate")
//    public void translateMessage(
//            @PathVariable Long roomId,
//            @PathVariable Long messageId,
//            @RequestParam Long userId,
//            @RequestParam String targetLanguage
//    ) {
//        chatService.translateMessage(
//                messageId,
//                roomId,
//                userId,
//                targetLanguage
//        );
//    }
//
//
//    /* =========================
//   채팅 나가기 (C_011)
//   POST /api/chat/rooms/{roomId}/leave
//========================= */
//    @PostMapping("/rooms/{roomId}/leave")
//    public void leaveChat(
//            @PathVariable Long roomId,
//            @RequestParam Long userId
//    ) {
//        chatService.leaveChat(roomId, userId);
//    }
//
//}
