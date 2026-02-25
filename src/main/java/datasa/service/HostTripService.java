package datasa.service;

import datasa.domain.dto.HostApplicationItem;
import datasa.domain.dto.TripListResponse;
import datasa.domain.entity.Application;
import datasa.domain.entity.Notification;
import datasa.domain.entity.Trip;
import datasa.domain.entity.TripCancel;
import datasa.domain.entity.User;
import datasa.repository.ApplicationRepository;
import datasa.repository.NotificationRepository;
import datasa.repository.TripCancelRepository;
import datasa.repository.TripRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HostTripService {

	private final TripRepository tripRepository;
	private final ApplicationRepository applicationRepository;
	private final UserRepository userRepository;
	private final NotificationRepository notificationRepository;
	private final TripCancelRepository tripCancelRepository;

	@Transactional(readOnly = true)
	public List<TripListResponse> getMyTrips(Long hostUserId) {
		return tripRepository.findByHostUser_UserIdOrderByCreatedAtDesc(hostUserId)
				.stream()
				.map(TripListResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<HostApplicationItem> getTripApplications(Long tripId, String hostEmail) {
		User host = userRepository.findByEmail(hostEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."));

		Trip trip = tripRepository.findByIdWithHostUser(tripId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "투어를 찾을 수 없습니다."));

		if (trip.getHostUser() == null || !trip.getHostUser().getUserId().equals(host.getUserId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "내 투어의 신청현황만 볼 수 있습니다.");
		}

		List<Application> apps = applicationRepository.findByTripIdWithUser(tripId);
		List<Long> ids = apps.stream().map(Application::getApplicationId).toList();

		Map<Long, String> cancelMap = tripCancelRepository.findByApplication_ApplicationIdIn(ids)
				.stream()
				.collect(Collectors.toMap(
						r -> r.getApplication().getApplicationId(),
						r -> r.getStatus().name()
				));

		return apps.stream()
				.map(a -> HostApplicationItem.from(a, cancelMap.get(a.getApplicationId())))
				.toList();
	}

	public void approveApplication(Long tripId, Long applicationId, String hostEmail) {
		Application a = getOwnedApplication(tripId, applicationId, hostEmail);
		if (a.getStatus() != Application.Status.PENDING) return;

		a.approve();

		Trip trip = a.getTrip();
		User applicant = a.getUser();
		if (applicant != null && applicant.getUserId() != null) {
			Notification n = Notification.tripApplicationApproved(applicant, trip.getTitle(), trip.getTripId());
			notificationRepository.save(n);
		}
	}

	public void rejectApplication(Long tripId, Long applicationId, String hostEmail) {
		Application a = getOwnedApplication(tripId, applicationId, hostEmail);
		if (a.getStatus() != Application.Status.PENDING) return;

		a.reject();

		Trip trip = a.getTrip();
		User applicant = a.getUser();
		if (applicant != null && applicant.getUserId() != null) {
			Notification n = Notification.tripApplicationRejected(applicant, trip.getTitle(), trip.getTripId());
			notificationRepository.save(n);
		}
	}

	public void approveCancel(Long tripId, Long applicationId, String hostEmail) {
		Application a = getOwnedApplication(tripId, applicationId, hostEmail);
		Trip trip = a.getTrip();

		if (trip.getStartAt() == null || !LocalDateTime.now().isBefore(trip.getStartAt())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "여정 시작 전까지만 처리할 수 있습니다.");
		}

		TripCancel req = tripCancelRepository.findByApplication_ApplicationId(applicationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "취소 신청이 없습니다."));

		if (req.getStatus() != TripCancel.Status.PENDING) return;

		req.approve();
		a.setStatus(Application.Status.CANCELED);

		// 취소 승인으로 자리가 비면, 아직 신청 가능 시간(시작 24시간 전 이전)이면 OPEN으로 다시 풀기
		if (trip.getStatus() == Trip.Status.CLOSED) {
			LocalDateTime lockAt = trip.getStartAt().minusHours(24);
			boolean canReopen = LocalDateTime.now().isBefore(lockAt);
			if (canReopen) {
				long approvedGuestCount = applicationRepository.countByTrip_TripIdAndStatus(
						trip.getTripId(), Application.Status.APPROVED
				);
				int maxGuests = trip.getMaxParticipants() - 1;
				if (approvedGuestCount < maxGuests) {
					trip.setStatus(Trip.Status.OPEN);
				}
			}
		}

		User applicant = a.getUser();
		if (applicant != null) {
			String title = trip.getTitle() == null ? "여정" : trip.getTitle().trim();
			Notification n = new Notification();
			setNotificationSystem(
					n,
					applicant,
					trip.getTripId(),
					"참가 취소 결과",
					"[" + title + "] 참여 취소 신청이 승인되었습니다."
			);
			notificationRepository.save(n);
		}
	}

	public void rejectCancel(Long tripId, Long applicationId, String hostEmail) {
		Application a = getOwnedApplication(tripId, applicationId, hostEmail);
		Trip trip = a.getTrip();

		if (trip.getStartAt() == null || !LocalDateTime.now().isBefore(trip.getStartAt())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "여정 시작 전까지만 처리할 수 있습니다.");
		}

		TripCancel req = tripCancelRepository.findByApplication_ApplicationId(applicationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "취소 신청이 없습니다."));

		if (req.getStatus() != TripCancel.Status.PENDING) return;
		req.reject();

		User applicant = a.getUser();
		if (applicant != null) {
			String title = trip.getTitle() == null ? "여정" : trip.getTitle().trim();
			Notification n = new Notification();
			setNotificationSystem(
					n,
					applicant,
					trip.getTripId(),
					"참가 취소 결과",
					"[" + title + "] 참여 취소 신청이 거부되었습니다."
			);
			notificationRepository.save(n);
		}
	}

	private Application getOwnedApplication(Long tripId, Long applicationId, String hostEmail) {
		// 소유권 검증(호스트 확인)
		getTripApplications(tripId, hostEmail);

		Application a = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "신청을 찾을 수 없습니다."));

		if (!a.getTrip().getTripId().equals(tripId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
		}
		return a;
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
