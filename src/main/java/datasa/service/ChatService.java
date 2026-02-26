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

import java.time.LocalDateTime;
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

    private final ApplicationService applicationService;


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
        validateNotReadOnly(trip);
        applicationService.validateApprovedUser(trip.getTripId(), userId);

        //  원문 메시지 저장
        ChatMessage message = new ChatMessage(
                room,
                sender,
                dto.getOriginalText()
        );
        chatMessageRepository.save(message);


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

        @Transactional
        public Long getRoomIdForEntry(Long tripId, Long userId) {

            //  승인 여부 검증
            applicationService.validateApprovedUser(tripId, userId);

            // 채팅방 조회
            ChatRoom room = chatRoomRepository.findByTrip_TripId(tripId)
                    .orElseThrow(() -> new IllegalStateException("채팅방 없음"));

            // ChatMember 검증
            ChatMember member = chatMemberRepository
                    .findByChatRoom_RoomIdAndUser_UserId(room.getRoomId(), userId)
                    .orElseThrow(() -> new IllegalStateException("채팅방 멤버 아님"));

            //  나갔다가 재입장 허용
            if (!member.isActive()) {
                member.rejoin();
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


        return page.map(ChatMessageResponseDto::from);



    }


    @Transactional
    public void leaveRoom(Long roomId, Long userId) {

        ChatMember member = chatMemberRepository
                .findByChatRoom_RoomIdAndUser_UserId(roomId, userId)
                .orElseThrow(() -> new IllegalStateException("채팅방 참여자가 아닙니다."));

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

        validateNotReadOnly(message.getChatRoom().getTrip());
        String translated = translationService.translate(
                message.getOriginalText(),
                targetLanguage
        );

        return ChatMessageResponseDto.builder()
                .messageId(message.getMessageId())
                .translatedText(translated)
                .build();
    }



    @Transactional(readOnly = true)
    public boolean isReadOnly(Trip trip) {
        if (trip == null || trip.getEndAt() == null) return false;
        return trip.getEndAt().plusDays(7).isBefore(LocalDateTime.now());
    }

    private void validateNotReadOnly(Trip trip) {
        if (isReadOnly(trip)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "여행 종료 후 1주일이 지나 채팅은 열람만 가능합니다."
            );
        }
    }

}

