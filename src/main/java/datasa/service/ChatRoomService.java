package datasa.service;

import datasa.domain.dto.ChatReadEventDto;
import datasa.domain.dto.ChatRoomListDto;

import datasa.domain.entity.*;
import datasa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final TripRepository tripRepository;
    private final ApplicationRepository applicationRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    public List<ChatRoomListDto> getMyChatRooms(Long userId) {

        Set<Trip> allTrips = new LinkedHashSet<>();
        allTrips.addAll(tripRepository.findByHostUser_UserIdOrderByCreatedAtDesc(userId));
        allTrips.addAll(applicationRepository.findApprovedTripsForChat(userId));

        return allTrips.stream().map(trip -> {

            ChatRoom room = chatRoomRepository
                    .findByTrip_TripId(trip.getTripId())
                    .orElseGet(() -> {
                        ChatRoom newRoom = new ChatRoom();
                        newRoom.setTrip(trip);
                        newRoom.setCreatedAt(LocalDateTime.now());
                        return chatRoomRepository.save(newRoom);
                    });

            // 1️⃣ 마지막 메시지
            Optional<ChatMessage> lastMsg =
                    chatMessageRepository.findTopByChatRoom_RoomIdOrderByCreatedAtDesc(
                            room.getRoomId()
                    );

            String lastMessage = lastMsg.map(ChatMessage::getOriginalText).orElse("");
            LocalDateTime lastAt = lastMsg.map(ChatMessage::getCreatedAt).orElse(null);

            // 2️⃣ readAt
            LocalDateTime readAt = chatMemberRepository
                    .findByChatRoom_RoomIdAndUser_UserId(room.getRoomId(), userId)
                    .map(ChatMember::getReadAt)
                    .orElse(null);

            long unreadCount;

            if (readAt == null) {
                unreadCount = chatMessageRepository
                        .countByChatRoom_RoomIdAndSender_UserIdNot(
                                room.getRoomId(),
                                userId
                        );
            } else {
                unreadCount = chatMessageRepository.countUnreadMessages(
                        room.getRoomId(),
                        userId,
                        readAt
                );
            }
            return new ChatRoomListDto(
                    room.getRoomId(),
                    trip.getTripId(),
                    trip.getTitle(),
                    lastMessage,
                    lastAt,
                    unreadCount
            );
        }).toList();
    }

    @Transactional
    public void markAsRead(Long roomId, Long userId) {

        ChatMember member = chatMemberRepository
                .findByChatRoom_RoomIdAndUser_UserId(roomId, userId)
                .orElseGet(() -> {
                    ChatMember m = new ChatMember();
                    m.setChatRoom(chatRoomRepository.getReferenceById(roomId));
                    m.setUser(userRepository.getReferenceById(userId));
                    m.setJoinedAt(LocalDateTime.now());
                    return chatMemberRepository.save(m);
                });

        // 🔥 핵심: 무조건 현재 시각으로 덮어쓰기
        member.setReadAt(LocalDateTime.now());
    }


}

