package datasa.domain.dto;

import datasa.domain.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
		Long notificationId,
		String type,
		Long refId,
		String title,
		String body,
		boolean isRead,
		LocalDateTime createdAt
) {
	public static NotificationResponse from(Notification n) {
		return new NotificationResponse(
				n.getNotificationId(),
				n.getType().name(),
				n.getRefId(),
				n.getTitle(),
				n.getBody(),
				Boolean.TRUE.equals(n.getIsRead()),
				n.getCreatedAt()
		);
	}
}
