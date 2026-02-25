package datasa.service;

import datasa.domain.entity.*;
import datasa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TripCancelRequestService {
	
	private final ApplicationRepository applicationRepository;
	private final TripCancelRepository tripCancelRequestRepository;
	private final NotificationRepository notificationRepository;
	
	public void requestCancel(Long applicationId, Long userId) {
		Application app = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "신청 정보를 찾을 수 없습니다."));
		
		if (app.getUser() == null || !app.getUser().getUserId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 신청만 취소할 수 있습니다.");
		}
		
		if (app.getStatus() != Application.Status.APPROVED) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "승인된 신청만 취소 신청이 가능합니다.");
		}
		
		Trip trip = app.getTrip();
		if (trip.getStartAt() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "시작 시간이 없는 투어입니다.");
		}
		
		LocalDateTime lockAt = trip.getStartAt().minusHours(24);
		if (!LocalDateTime.now().isBefore(lockAt)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "여정 시작 24시간 전까지만 취소 신청이 가능합니다.");
		}
		
		TripCancel existing = tripCancelRequestRepository
				.findByApplication_ApplicationId(applicationId)
				.orElse(null);
		
		if (existing != null && existing.getStatus() == TripCancel.Status.PENDING) {
			return;
		}
		
		TripCancel req = TripCancel.pending(app);
		tripCancelRequestRepository.save(req);
		
		User host = trip.getHostUser();
		if (host != null && host.getUserId() != null) {
			String actor = app.getUser().getName() == null ? "누군가" : app.getUser().getName().trim();
			String title = trip.getTitle() == null ? "여정" : trip.getTitle().trim();
			
			Notification n = new Notification();
			// SYSTEM 타입으로 통일 (DB enum 수정 없이)
			setNotificationSystem(n, host, trip.getTripId(),
					"참가 취소 신청",
					actor + "님이 " + title + "여정에 참여 취소를 신청했습니다"
			);
			notificationRepository.save(n);
		}
	}
	
	private void setNotificationSystem(Notification n, User user, Long refId, String title, String body) {
		try {
			var fUser = Notification.class.getDeclaredField("user");
			var fType = Notification.class.getDeclaredField("type");
			var fRefId = Notification.class.getDeclaredField("refId");
			var fTitle = Notification.class.getDeclaredField("title");
			var fBody = Notification.class.getDeclaredField("body");
			var fRead = Notification.class.getDeclaredField("isRead");
			fUser.setAccessible(true);
			fType.setAccessible(true);
			fRefId.setAccessible(true);
			fTitle.setAccessible(true);
			fBody.setAccessible(true);
			fRead.setAccessible(true);
			
			fUser.set(n, user);
			fType.set(n, Notification.Type.SYSTEM);
			fRefId.set(n, refId);
			fTitle.set(n, title);
			fBody.set(n, body.length() > 255 ? body.substring(0, 252) + "..." : body);
			fRead.set(n, false);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "알림 생성 실패");
		}
	}
}