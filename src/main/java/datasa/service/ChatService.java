package datasa.service;

import datasa.repository.UserRepository;
import datasa.service.TranslationService;
import datasa.repository.ChatMemberRepository;
import datasa.repository.ChatMessageRepository;
import datasa.repository.ChatRoomRepository;
import datasa.domain.entity.ChatMember;
import datasa.domain.entity.ChatMessage;
import datasa.domain.entity.ChatRoom;
import datasa.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TranslationService translationService;
    private final UserRepository userRepository;

    /* =========================
       공통: 채팅 권한 체크 (C_006)
    ========================= */
    private ChatMember validateChatAccess(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return chatMemberRepository
                .findByChatRoomAndUserAndLeftAtIsNull(room, user)
                .orElseThrow(() -> new IllegalStateException("채팅 권한 없음"));
    }

    /* =========================
       메시지 전송 (저장)
    ========================= */
    public void sendMessage(Long roomId, Long userId, String message) {

        ChatMember member = validateChatAccess(roomId, userId);

        ChatMessage chatMessage = new ChatMessage(
                member.getChatRoom(),
                member.getUser(),
                message
        );

        chatMessageRepository.save(chatMessage);
    }

    /* =========================
       메시지 조회 (페이징)
    ========================= */
    @Transactional(readOnly = true)
    public Page<ChatMessage> getMessages(
            Long roomId,
            Long userId,
            Pageable pageable
    ) {
        validateChatAccess(roomId, userId);

        return chatMessageRepository.findByChatRoom_RoomIdOrderByCreatedAtAsc(
                roomId, pageable
        );
    }

    @Transactional
    public void translateMessage(
            Long messageId,
            Long roomId,
            Long userId,
            String targetLanguage
    ) {
        // 1️⃣ 채팅 권한 체크
        validateChatAccess(roomId, userId);

        // 2️⃣ 메시지 조회
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지가 존재하지 않습니다."));

        // 3️⃣ 번역 실행
        String translated = translationService.translate(
                message.getOriginalText(),
                targetLanguage
        );

        // 4️⃣ 번역 결과 저장
        message.applyTranslation(translated, targetLanguage);
    }


    @Transactional
    public void updateReadAt(Long roomId, Long userId) {

        ChatMember member = validateChatAccess(roomId, userId);

        member.markAsRead();
    }


    @Transactional
    public void leaveChat(Long roomId, Long userId) {

        // 1️⃣ 권한 체크 (나간 사람은 여기서 바로 예외)
        ChatMember member = validateChatAccess(roomId, userId);

        // 2️⃣ 나가기 처리
        member.leave();
    }

}
