
package datasa.controller;

import datasa.domain.dto.ChatMessageResponseDto;
import datasa.domain.entity.ChatMessage;
import datasa.repository.ChatMessageRepository;
import datasa.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    //채팅 히스토리
    @GetMapping("/rooms/{roomId}/messages")
    public Page<ChatMessageResponseDto> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return chatService.getMessages(roomId, pageable);
    }

    //채팅 읽음 표시
    @PostMapping("/rooms/{roomId}/read")
    public void read(
            @PathVariable Long roomId,
            @RequestParam Long userId
    ) {
        chatService.markAsRead(roomId, userId);
    }

    @PostMapping("/messages/{messageId}/translate")
    public ResponseEntity<Void> translateMessage(
            @PathVariable Long messageId,
            @RequestParam String targetLanguage
    ) {
        chatService.translateMessage(messageId, targetLanguage);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/rooms/{roomId}/leave")
    public void leaveRoom(
            @PathVariable Long roomId,
            @RequestParam Long userId
    ) {
        chatService.leaveRoom(roomId, userId);
    }
}



