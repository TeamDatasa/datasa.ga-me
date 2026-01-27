package datasa.service;

import datasa.domain.dto.ChatMessageRequestDto;
import datasa.domain.dto.ChatMessageResponseDto;
import datasa.domain.entity.ChatMessage;
import datasa.domain.entity.ChatRoom;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.ChatMessageRepository;
import datasa.repository.ChatRoomRepository;
import datasa.repository.TripRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    // ✅ 이미 프로젝트에 있는 "승인 사용자만 채팅 가능" 검증 로직을 여기에 연결하면 됨
    // (아직 메서드가 없다면 내가 만들어줄게)
    private final ApplicationService applicationService;

    // 번역 기능이 실제로 있다면 주입해서 사용
    // private final TranslationService translationService;

    /**
     * 실시간 메시지 저장 + (선택) 번역 적용.
     * WebSocket에서는 userId/roomId 위변조 방지를 위해
     * userId는 세션에서, roomId는 destination variable에서만 받는다.
     */
    @Transactional
    public ChatMessageResponseDto sendMessage(Long roomId, Long userId, ChatMessageRequestDto dto) {
        validateRequest(dto);

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다. roomId=" + roomId));

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다. userId=" + userId));

        // ✅ 승인 사용자만 채팅 가능 (핵심)
        // ChatRoom은 Trip과 1:1이므로 tripId를 꺼내서 검증
        Trip trip = room.getTrip();
        applicationService.validateApprovedUser(trip.getTripId(), userId);

        // 원문 저장
        ChatMessage message = new ChatMessage(room, sender, dto.getOriginalText());

        // (선택) 번역 요청이 있는 경우
        if (dto.getTargetLanguage() != null && !dto.getTargetLanguage().isBlank()) {
            String targetLang = dto.getTargetLanguage().trim();

            // 실제 번역 서비스가 있으면 여기서 호출
            // String translated = translationService.translate(dto.getOriginalText(), targetLang);

            // 일단 MVP에서는 translatedText를 비워두거나, 테스트로 원문 그대로 넣어도 됨
            // String translated = dto.getOriginalText();
            String translated = null;

            if (translated != null && !translated.isBlank()) {
                message.applyTranslation(translated, targetLang);
            }
        }

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

    /**
     * (선택) tripId로 방 생성/조회가 필요하면 이 메서드로 통일하면 좋음
     * - trip 1개 = chatroom 1개
     */
    @Transactional
    public Long getOrCreateRoomIdByTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("여행이 존재하지 않습니다. tripId=" + tripId));

        return chatRoomRepository.findByTrip_TripId(tripId)
                .map(ChatRoom::getRoomId)
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom(trip);
                    chatRoomRepository.save(newRoom);
                    return newRoom.getRoomId();
                });
    }
}

