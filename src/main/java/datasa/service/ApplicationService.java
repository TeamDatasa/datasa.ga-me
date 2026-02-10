package datasa.service;

import datasa.domain.dto.ApplicationCreateResponseDto;
import datasa.domain.dto.ApplicationListResponseDto;
import datasa.domain.dto.MyApplicationDetailDto;
import datasa.domain.entity.*;
import datasa.repository.*;
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
    private final ChatRoomService chatRoomService;
	private final NotificationRepository notificationRepository;
	
	
	/**
     * U_004 여행 신청
     */
	// trip 신청
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
		
		// 신청 알림 생성 (호스트한테)
		User host = trip.getHostUser();
		if (host != null && host.getUserId() != null && !host.getUserId().equals(userId)) {
			Notification n = Notification.tripApply(
					host,
					user.getName(),
					trip.getTitle(),
					trip.getTripId(),
					app.getApplicationId()
			);
			notificationRepository.save(n);
		}
		
		return new ApplicationCreateResponseDto(
				app.getApplicationId(),
				tripId,
				app.getStatus().name()
		);
	}


    /**
     * U_005 내 신청 상태 조회 (목록)
     * 해당 여행에 누가 신청했는지 전부 보여주기
     */
    @Transactional(readOnly = true)
    public List<ApplicationListResponseDto> getApplicationsByTrip(Long tripId) {

        return applicationRepository.findByTrip_TripId(tripId)
                .stream()
                .map(app -> new ApplicationListResponseDto(
                        app.getApplicationId(),
                        app.getUser().getUserId(),
                        app.getUser().getName(),
                        app.getStatus(),
                        app.getCreatedAt()
                ))
                .toList();
    }

    // 내가 이 여행을 신청했는지 여부 확인
    @Transactional(readOnly = true)
    public boolean hasApplied(Long tripId, Long userId) {
        return applicationRepository.existsByTrip_TripIdAndUser_UserId(tripId, userId);
    }


    // 호스트 신청 관리
    public List<Application> getApplicationsByTripForHost(Long tripId, Long hostUserId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("여행 없음"));

        // 🔒 호스트 권한 체크
        if (!trip.getHostUser().getUserId().equals(hostUserId)) {
            throw new AccessDeniedException("신청자 목록 조회 권한 없음");
        }

        return applicationRepository.findByTrip_TripId(tripId);
    }


    // 신청 승인
    @Transactional
    public void approve(Long applicationId, Long hostUserId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청 없음"));

        Trip trip = tripRepository.findByIdForUpdate(app.getTrip().getTripId())
                .orElseThrow(() -> new IllegalArgumentException("여행 없음"));

        // 권한 / 상태 체크 생략

        app.approve();
        applicationRepository.saveAndFlush(app);

        // 🔥 여기서 chat_room 먼저 생성
        ChatRoom room = chatRoomRepository
                .findByTrip_TripId(trip.getTripId())
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setTrip(trip);
                    newRoom.setCreatedAt(LocalDateTime.now());
                    return chatRoomRepository.saveAndFlush(newRoom);
                });

        // 🔥 host member
        createChatMemberIfAbsent(room, trip.getHostUser());

        // 🔥 승인된 유저 member
        createChatMemberIfAbsent(room, app.getUser());
    }



    //신청 거절
    @Transactional
    public void reject(Long applicationId, Long hostUserId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청 없음"));

        Trip trip = app.getTrip();

        if (!trip.getHostUser().getUserId().equals(hostUserId)) {
            throw new AccessDeniedException("거절 권한 없음");
        }

        if (app.getStatus() != Application.Status.PENDING) {
            throw new IllegalStateException("이미 처리된 신청");
        }

        app.reject();
    }



    @Transactional(readOnly = true)
    public void validateApprovedUser(Long tripId, Long userId) {
        // 개발용: 항상 통과
        return;
    }


    @Transactional(readOnly = true)
    public List<MyApplicationDetailDto> getMyApplicationDetails(Long userId) {

        return applicationRepository
                .findTop20ByUser_UserIdOrderByApplicationIdDesc(userId)
                .stream()
                .map(MyApplicationDetailDto::from)
                .toList();
    }

    private void createChatMemberIfAbsent(ChatRoom room, User user) {
        chatMemberRepository
                .findByChatRoom_RoomIdAndUser_UserId(room.getRoomId(), user.getUserId())
                .ifPresentOrElse(
                        member -> {
                            if (!member.isActive()) {
                                member.rejoin();
                            }
                        },
                        () -> {
                            ChatMember member = new ChatMember();
                            member.setChatRoom(room);
                            member.setUser(user);
                            member.setJoinedAt(LocalDateTime.now());
                            chatMemberRepository.save(member);
                        }
                );
    }







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

