package datasa.service;

import datasa.domain.dto.ApplicationCreateResponseDto;
import datasa.domain.dto.ApplicationListResponseDto;
import datasa.domain.entity.Application;
import datasa.repository.ChatMemberRepository;
import datasa.repository.ChatRoomRepository;
import datasa.domain.entity.ChatMember;
import datasa.domain.entity.ChatRoom;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.ApplicationRepository;
import datasa.repository.TripRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;


    /**
     * U_004 여행 신청
     */
    @Transactional
    public ApplicationCreateResponseDto applyTrip(Long tripId, Long userId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("여행이 존재하지 않습니다."));

        if (trip.getStatus() != Trip.Status.OPEN) {
            throw new IllegalStateException("신청 가능한 여행이 아닙니다.");
        }

        if (applicationRepository.existsByTrip_TripIdAndUser_UserId(tripId, userId)) {
            throw new IllegalStateException("이미 신청한 여행입니다.");
        }

        long approvedCount =
                applicationRepository.countByTrip_TripIdAndStatus(
                        tripId, Application.Status.APPROVED
                );

        if (approvedCount >= trip.getMaxParticipants()) {
            throw new IllegalStateException("정원이 초과되었습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Application app = new Application();
        app.setTrip(trip);
        app.setUser(user);
        app.setStatus(Application.Status.PENDING);

        applicationRepository.save(app);

        return new ApplicationCreateResponseDto(
                app.getApplicationId(),
                tripId,
                app.getStatus().name()
        );
    }

    /**
     * U_005 내 신청 상태 조회
     */
    @Transactional(readOnly = true)
    public  List<ApplicationListResponseDto> getApplicationsByTrip(Long tripId) {

        return applicationRepository.findByTrip_TripId(tripId)
                .stream()
                .map(app -> new ApplicationListResponseDto(
                        app.getApplicationId(),
                        app.getUser().getUserId(),
                        app.getUser().getName(),
                        app.getStatus().name(),
                        app.getCreatedAt()
                ))
                .toList();
    }

    // 신청 승인
    @Transactional
    public void approve(Long applicationId, Long hostUserId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청이 존재하지 않습니다."));

        Trip trip = app.getTrip();

        // 1) 호스트만 승인 가능
        if (!trip.getHostUser().getUserId().equals(hostUserId)) {
            throw new IllegalStateException("호스트만 승인할 수 있습니다.");
        }

        // 2) PENDING만 처리
        if (app.getStatus() != Application.Status.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }

        // 3) 정원 체크(승인 전)
        long approvedCount = applicationRepository.countByTrip_TripIdAndStatus(
                trip.getTripId(), Application.Status.APPROVED
        );
        if (approvedCount >= trip.getMaxParticipants()) {
            throw new IllegalStateException("정원이 초과되었습니다.");
        }

        // 4) 승인 처리
        app.setStatus(Application.Status.APPROVED);

        // ==========================
        // 5) 승인 시 채팅방 생성/참여
        // ==========================
        ChatRoom room = chatRoomRepository.findByTrip(trip)
                .orElseGet(() -> {
                    ChatRoom newRoom = chatRoomRepository.save(new ChatRoom(trip));

                    // 호스트 자동 참여
                    chatMemberRepository.save(new ChatMember(newRoom, trip.getHostUser()));
                    return newRoom;
                });

        // 승인된 신청자 참여(중복 방지)
        chatMemberRepository.findByChatRoomAndUser(room, app.getUser())
                .orElseGet(() -> chatMemberRepository.save(new ChatMember(room, app.getUser())));

        // ==========================
        // 6) 승인 후 정원 도달 시 CLOSED
        // ==========================
        long afterApprovedCount = approvedCount + 1;
        if (afterApprovedCount >= trip.getMaxParticipants()) {
            trip.setStatus(Trip.Status.CLOSED);
        }
    }






    //신청 거절
    @Transactional
    public void reject(Long applicationId, Long hostUserId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청이 존재하지 않습니다."));

        // 1️⃣ 호스트 권한 체크
        Long realHostId = app.getTrip().getHostUser().getUserId();
        if (!realHostId.equals(hostUserId)) {
            throw new IllegalStateException("호스트만 거절할 수 있습니다.");
        }

        // 2️⃣ 상태 체크
        if (app.getStatus() != Application.Status.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }

        // 3️⃣ 거절 처리
        app.setStatus(Application.Status.REJECTED);
        app.setDecidedAt(LocalDateTime.now());
    }


    @Transactional(readOnly = true)
    public void validateApprovedUser(Long tripId, Long userId) {
        // 개발용: 항상 통과
        return;
    }

//인증필요
//    public void validateApprovedUser(Long tripId, Long userId) {
//
//
//        boolean approved = applicationRepository
//                .existsByTrip_TripIdAndUser_UserIdAndStatus(
//                        tripId,
//                        userId,
//                        Application.Status.APPROVED
//                );
//
//        if (!approved) {
//            throw new AccessDeniedException("승인된 사용자만 접근할 수 있습니다.");
//        }
//    }
}
