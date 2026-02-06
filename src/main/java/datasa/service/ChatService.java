package datasa.service;

import datasa.domain.dto.ChatMessageRequestDto;
import datasa.domain.dto.ChatMessageResponseDto;
import datasa.domain.dto.ChatRoomResponseDto;
import datasa.domain.entity.*;
import datasa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ApplicationRepository applicationRepository;

    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final SimpMessagingTemplate messagingTemplate;
    // ✅ 이미 프로젝트에 있는 "승인 사용자만 채팅 가능" 검증 로직을 여기에 연결하면 됨

    private final ApplicationService applicationService;

    // 번역 기능이 실제로 있다면 주입해서 사용
    // private final TranslationService translationService;

    /**
     * 실시간 메시지 저장 + (선택) 번역 적용.
     * WebSocket에서는 userId/roomId 위변조 방지를 위해
     * userId는 세션에서, roomId는 destination variable에서만 받는다.
     */
    @Transactional
    public ChatMessageResponseDto sendMessage(
            Long roomId,
            Long userId,
            ChatMessageRequestDto dto
    ) {
        validateRequest(dto);

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다. roomId=" + roomId));

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다. userId=" + userId));

        // 승인 사용자만 채팅 가능
        Trip trip = room.getTrip();
        applicationService.validateApprovedUser(trip.getTripId(), userId);

        //  원문 메시지 저장 (번역 X)
        ChatMessage message = new ChatMessage(
                room,
                sender,
                dto.getOriginalText()
        );
        chatMessageRepository.save(message);


        //  원문 DTO 즉시 반환 (WS 전송용)
        return ChatMessageResponseDto.from(message);
    }


    private void validateRequest(ChatMessageRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("메시지 요청이 비어있습니다.");
        }
        if (dto.getOriginalText() == null || dto.getOriginalText().isBlank()) {
            throw new IllegalArgumentException("메시지(originalText)는 필수입니다.");
        }
        if (dto.getOriginalText().length() > 2000) {
            throw new IllegalArgumentException("메시지가 너무 깁니다(최대 2000자).");
        }
        if (dto.getTargetLanguage() != null && dto.getTargetLanguage().length() > 10) {
            throw new IllegalArgumentException("targetLanguage가 너무 깁니다(최대 10자).");
        }
    }


    /**
     * (선택) tripId로 방 생성/조회가 필요하면 이 메서드로 통일하면 좋음
     * - trip 1개 = chatroom 1개
     */

        @Transactional
        public Long getRoomIdForEntry(Long tripId, Long userId) {

            // 1️⃣ 승인 여부 검증
            applicationService.validateApprovedUser(tripId, userId);

            // 2️⃣ 채팅방 조회
            ChatRoom room = chatRoomRepository.findByTrip_TripId(tripId)
                    .orElseThrow(() -> new IllegalStateException("채팅방 없음"));

            // 3️⃣ ChatMember 검증
            ChatMember member = chatMemberRepository
                    .findByChatRoom_RoomIdAndUser_UserId(room.getRoomId(), userId)
                    .orElseThrow(() -> new IllegalStateException("채팅방 멤버 아님"));

            // 4️⃣ 나갔다가 재입장 허용
            if (!member.isActive()) {
                member.rejoin(); // leftAt = null
            }

            return room.getRoomId();
        }


    //메세지 조회
    @Transactional(readOnly = true)
    public Page<ChatMessageResponseDto> getMessages(Long roomId, Pageable pageable) {

        Page<ChatMessage> page =
                chatMessageRepository.findByChatRoom_RoomIdOrderByCreatedAtDesc(
                        roomId, pageable
                );

        // Entity → DTO 변환
        return page.map(ChatMessageResponseDto::from);



    }


    @Transactional
    public void leaveRoom(Long roomId, Long userId) {

        ChatMember member = chatMemberRepository
                .findByChatRoom_RoomIdAndUser_UserId(roomId, userId)
                .orElseThrow(() -> new IllegalStateException("채팅방 참여자가 아닙니다."));

        // 이미 나간 경우 방어
        if (!member.isActive()) {
            return;
        }

        member.leave();
    }

//읽음 처리
    @Transactional
    public void markAsRead(Long roomId, Long userId) {
        ChatMember member = chatMemberRepository
                .findByChatRoom_RoomIdAndUser_UserId(roomId, userId)
                .orElseThrow(() -> new IllegalStateException("채팅방 참여자가 아닙니다."));

        // 나간 사용자는 읽음 처리 X
        if (!member.isActive()) {
            return;
        }

        member.markAsRead();
    }



    private final TranslationService translationService;


    @Transactional(readOnly = true)
    public ChatMessageResponseDto translateMessage(
            Long messageId,
            String targetLanguage
    ) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지 없음"));

        String translated = translationService.translate(
                message.getOriginalText(),
                targetLanguage
        );

        return ChatMessageResponseDto.builder()
                .messageId(message.getMessageId())
                .translatedText(translated)
                .build();
    }


}

