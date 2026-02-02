package datasa.service;

import datasa.domain.dto.TripCommentedEvent;
import datasa.domain.dto.TripLikedEvent;
import datasa.domain.entity.Notification;
import datasa.domain.entity.User;
import datasa.repository.NotificationRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
	
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onTripLiked(TripLikedEvent event) {
		User ownerRef = userRepository.getReferenceById(event.ownerUserId());
		Notification n = Notification.tripLike(
				ownerRef,
				event.actorUserId(),
				event.tripId()
		);
		notificationRepository.save(n);
	}
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onTripCommented(TripCommentedEvent event) {
		User ownerRef = userRepository.getReferenceById(event.ownerUserId()); // 추가
		Notification n = Notification.tripComment(
				ownerRef,
				event.actorUserId(),
				event.tripId(),
				event.commentId()
		);
		notificationRepository.save(n);
	}
}
