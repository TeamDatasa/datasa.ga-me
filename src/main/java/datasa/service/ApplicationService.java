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
	private final ChatRoomNotificationService chatRoomNotificationService;
	
	
	// trip 신청
	@Transactional
	public ApplicationCreateResponseDto applyTrip(Long tripId, Long userId) {
		
		Trip trip = tripRepository.findById(tripId)
				.orElseThrow(() -> new IllegalArgumentException("여행이 존재하지 않습니다."));
		
		if (trip.getStatus() != Trip.Status.OPEN) {
			throw new IllegalStateException("신청 가능한 여행이 아닙니다.");
		}
		
		// 호스트(작성자)는 본인 여행에 신청 불가
		if (trip.getHostUser() != null
				&& trip.getHostUser().getUserId() != null
				&& trip.getHostUser().getUserId().equals(userId)) {
			throw new IllegalStateException("호스트는 본인 여행에 신청할 수 없습니다.");
		}
		
		// 시작일 기준 24시간 전부터 신청 불가
		if (trip.getStartAt() != null) {
			LocalDateTime lockAt = trip.getStartAt().minusHours(24);
			if (!LocalDateTime.now().isBefore(lockAt)) {
				throw new IllegalStateException("여정 시작 24시간 전부터는 신청할 수 없습니다.");
			}
		}
		
		if (applicationRepository.existsByTrip_TripIdAndUser_UserId(tripId, userId)) {
			throw new IllegalStateException("이미 신청한 여행입니다.");
		}
		
		// 현재 승인된 게스트 수
		long approvedGuestCount =
				applicationRepository.countByTrip_TripIdAndStatus(
						tripId, Application.Status.APPROVED
				);
		
		// 호스트 1명은 기본 참가자이므로, 승인 가능한 게스트 최대치는 (정원 - 1)
		int maxParticipants = trip.getMaxParticipants();
		int maxGuests = maxParticipants - 1;
		
		if (approvedGuestCount >= maxGuests) {
			throw new IllegalStateException("정원이 초과되었습니다.");
		}
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
		
		Application app = new Application();
		app.setTrip(trip);
		app.setUser(user);
		app.setStatus(Application.Status.PENDING);
		
		applicationRepository.save(app);
		
		// 신청 알림 생성 (호스트에게)
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
		
		// 호스트 권한 체크
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
		
		// 동시 승인 경쟁 방지: Trip row lock
		Trip trip = tripRepository.findByIdForUpdate(app.getTrip().getTripId())
				.orElseThrow(() -> new IllegalArgumentException("여행 없음"));
		
		// 호스트 권한 체크
		if (!trip.getHostUser().getUserId().equals(hostUserId)) {
			throw new AccessDeniedException("승인 권한 없음");
		}
		
		// 상태 체크(중복 승인 방지)
		if (app.getStatus() != Application.Status.PENDING) {
			throw new IllegalStateException("이미 처리된 신청");
		}
		
		// 승인 직전 정원 체크 (호스트 1명 포함 기준)
		long approvedGuestCount =
				applicationRepository.countByTrip_TripIdAndStatus(
						trip.getTripId(), Application.Status.APPROVED
				);
		
		int maxParticipants = trip.getMaxParticipants();
		int maxGuests = maxParticipants - 1;
		
		if (approvedGuestCount >= maxGuests) {
			throw new IllegalStateException("정원이 초과되었습니다.");
		}
		
		// 승인 처리
		app.approve();
		applicationRepository.saveAndFlush(app);
		
		// 신청자에게 승인 알림 생성
		Notification approved = Notification.tripApplicationApproved(app.getUser(), trip.getTitle(), trip.getTripId());
		notificationRepository.save(approved);
		
		// 채팅방 생성/조회
		ChatRoom room = chatRoomRepository.findByTrip_TripId(trip.getTripId())
				.orElseGet(() -> {
					ChatRoom newRoom = new ChatRoom();
					newRoom.setTrip(trip);
					newRoom.setCreatedAt(LocalDateTime.now());
					return chatRoomRepository.saveAndFlush(newRoom);
				});

	// host member
		MemberJoinResult hostJoinResult = createChatMemberIfAbsent(room, trip.getHostUser());
		if (hostJoinResult == MemberJoinResult.CREATED || hostJoinResult == MemberJoinResult.REJOINED) {
			chatRoomNotificationService.ensureChatRoomCreatedNotification(trip.getHostUser(), trip);
		}

	// 승인된 유저 member ("멤버로 처음 들어가는 순간" 판별)
		MemberJoinResult joinResult = createChatMemberIfAbsent(room, app.getUser());

	// 신청자가 채팅방 멤버가 된 "순간"(신규/재참여)에만 알림 저장 (중복 방지)
		if (joinResult == MemberJoinResult.CREATED || joinResult == MemberJoinResult.REJOINED) {
			chatRoomNotificationService.ensureChatRoomCreatedNotification(app.getUser(), trip);
		}
		
		// 승인 후 정원 도달 시 모집 마감(CLOSED)
		long afterApprovedGuestCount =
				applicationRepository.countByTrip_TripIdAndStatus(
						trip.getTripId(), Application.Status.APPROVED
				);
		
		long totalParticipants = afterApprovedGuestCount + 1; // 호스트 1명 포함
		
		if (totalParticipants >= maxParticipants) {
			trip.close();
			tripRepository.saveAndFlush(trip);
		}
	}
	
	// 신청 거절
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
		applicationRepository.saveAndFlush(app);
		
		// 신청자에게 거절 알림 생성
		Notification rejected = Notification.tripApplicationRejected(app.getUser(), trip.getTitle(), trip.getTripId());
		notificationRepository.save(rejected);
	}
	
	@Transactional(readOnly = true)
	public void validateApprovedUser(Long tripId, Long userId) {
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
	
	private enum MemberJoinResult {
		CREATED,
		REJOINED,
		EXISTING
	}
	
	private MemberJoinResult createChatMemberIfAbsent(ChatRoom room, User user) {
		if (room == null || user == null || user.getUserId() == null) {
			throw new IllegalArgumentException("채팅 멤버 생성에 필요한 값이 없습니다.");
		}
		
		var opt = chatMemberRepository.findByChatRoom_RoomIdAndUser_UserId(room.getRoomId(), user.getUserId());
		
		if (opt.isPresent()) {
			ChatMember member = opt.get();
			if (!member.isActive()) {
				member.rejoin();
				return MemberJoinResult.REJOINED;
			}
			return MemberJoinResult.EXISTING;
		}
		
		ChatMember member = new ChatMember();
		member.setChatRoom(room);
		member.setUser(user);
		member.setJoinedAt(LocalDateTime.now());
		chatMemberRepository.save(member);
		return MemberJoinResult.CREATED;
	}
	
}