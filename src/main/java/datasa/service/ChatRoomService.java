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


    /**
     * 🔗 여행 신청 승인 시 채팅방 연결
     * - trip 당 chatRoom 1개
     * - host + 승인된 user를 ChatMember로 연결
     */
    @Transactional
    public void connectChatMember(Trip trip, User approvedUser) {

        // 1️⃣ ChatRoom 조회 or 생성
        ChatRoom room = chatRoomRepository
                .findByTrip_TripId(trip.getTripId())
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setTrip(trip);
                    newRoom.setCreatedAt(LocalDateTime.now());
                    return chatRoomRepository.save(newRoom);
                });

        // 2️⃣ 호스트 ChatMember 생성 (없으면)
        User host = trip.getHostUser();
        chatMemberRepository
                .findByChatRoom_RoomIdAndUser_UserId(room.getRoomId(), host.getUserId())
                .orElseGet(() -> {
                    ChatMember hostMember = new ChatMember();
                    hostMember.setChatRoom(room);
                    hostMember.setUser(host);
                    hostMember.setJoinedAt(LocalDateTime.now());
                    return chatMemberRepository.save(hostMember);
                });

        // 3️⃣ 승인된 사용자 ChatMember 생성 or 복구
        chatMemberRepository
                .findByChatRoom_RoomIdAndUser_UserId(room.getRoomId(), approvedUser.getUserId())
                .ifPresentOrElse(
                        member -> {
                            // 이미 있었는데 나간 상태라면 재입장 처리
                            if (!member.isActive()) {
                                member.rejoin();
                            }
                        },
                        () -> {
                            ChatMember member = new ChatMember();
                            member.setChatRoom(room);
                            member.setUser(approvedUser);
                            member.setJoinedAt(LocalDateTime.now());
                            chatMemberRepository.save(member);
                        }
                );
    }

}

