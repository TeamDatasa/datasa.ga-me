package datasa.service;

import datasa.domain.dto.TripCommentedEvent;
import datasa.domain.dto.TripLikedEvent;
import datasa.domain.entity.Notification;
import datasa.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
	
	private final NotificationRepository notificationRepository;
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onTripLiked(TripLikedEvent event) {
		Notification n = Notification.tripLike(
				event.ownerUserId(),
				event.actorUserId(),
				event.tripId()
		);
		notificationRepository.save(n);
	}
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onTripCommented(TripCommentedEvent event) {
		Notification n = Notification.tripComment(
				event.ownerUserId(),
				event.actorUserId(),
				event.tripId(),
				event.commentId()
		);
		notificationRepository.save(n);
	}
}