package datasa.service;

import datasa.domain.entity.Application;
import datasa.domain.entity.Notification;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.ApplicationRepository;
import datasa.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomNotificationService {
	
	private final NotificationRepository notificationRepository;
	private final ApplicationRepository applicationRepository;
	
	@Transactional
	public void ensureChatRoomCreatedNotification(User targetUser, Trip trip) {
		if (targetUser == null || targetUser.getUserId() == null) return;
		if (trip == null || trip.getTripId() == null) return;
		
		boolean alreadyExists = notificationRepository.existsByUser_UserIdAndTypeAndRefId(
				targetUser.getUserId(),
				Notification.Type.CHAT,
				trip.getTripId()
		);
		if (alreadyExists) return;
		
		Notification n = Notification.tripChatRoomCreated(targetUser, trip.getTitle(), trip.getTripId());
		notificationRepository.save(n);
	}
	
	@Transactional
	public void notifyAllApprovedMembersWhenRoomCreated(Trip trip) {
		if (trip == null || trip.getTripId() == null) return;
		
		// 호스트
		ensureChatRoomCreatedNotification(trip.getHostUser(), trip);
		
		// 승인된 참여자
		List<Application> apps = applicationRepository.findByTripIdWithUser(trip.getTripId());
		for (Application app : apps) {
			if (app.getStatus() == Application.Status.APPROVED) {
				ensureChatRoomCreatedNotification(app.getUser(), trip);
			}
		}
	}
}