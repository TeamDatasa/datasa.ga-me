package datasa.service;

import datasa.domain.dto.TripCommentedEvent;
import datasa.domain.entity.Notification;
import datasa.domain.entity.User;
import datasa.repository.NotificationRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
	
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onTripCommented(TripCommentedEvent event) {
		log.info("[NOTI] TripCommentedEvent received: tripId={}, commentId={}, actor={}, owner={}",
				event.tripId(), event.commentId(), event.actorUserId(), event.ownerUserId());
		
		if (event.actorUserId().equals(event.ownerUserId())) {
			log.info("[NOTI] skip (actor == owner)");
			return;
		}
		
		User ownerRef = userRepository.getReferenceById(event.ownerUserId());
		
		Notification n = Notification.tripComment(
				ownerRef,
				event.actorUserId(),
				event.tripId(),
				event.commentId()
		);
		
		// ✅ flush까지 강제해서 실제 INSERT 확인
		notificationRepository.saveAndFlush(n);
		
		log.info("[NOTI] saved+flushed notification: id={}, type={}, refId(tripId)={}",
				n.getNotificationId(), n.getType(), n.getRefId());
	}
}
