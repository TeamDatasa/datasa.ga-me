package datasa.service;

import datasa.domain.dto.TripCommentedEvent;
import datasa.domain.dto.TripLikedEvent;
import datasa.domain.entity.Notification;
import datasa.domain.entity.User;
import datasa.repository.NotificationRepository;
import datasa.repository.TripRepository;
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
	private final TripRepository tripRepository;
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void onTripLiked(TripLikedEvent event) {
		log.info("[Notif] onTripLiked fired. tripId={}, actor={}, owner={}",
				event.tripId(), event.actorUserId(), event.ownerUserId());
		
		if (event.actorUserId().equals(event.ownerUserId())) return;
		
		User owner = userRepository.findById(event.ownerUserId())
				.orElseThrow(() -> new IllegalArgumentException("owner user not found: " + event.ownerUserId()));
		
		String actorName = userRepository.findById(event.actorUserId())
				.map(User::getName)
				.orElse("누군가");
		
		String tripTitle = tripRepository.findById(event.tripId())
				.map(t -> t.getTitle())
				.orElse("내 게시글");
		
		Notification n = Notification.tripLike(owner, actorName, tripTitle, event.tripId());
		
		Notification saved = notificationRepository.saveAndFlush(n);
		log.info("[Notif] saved notificationId={}", saved.getNotificationId());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void onTripCommented(TripCommentedEvent event) {
		log.info("[Notif] onTripCommented fired. tripId={}, commentId={}, actor={}, owner={}",
				event.tripId(), event.commentId(), event.actorUserId(), event.ownerUserId());

		if (event.actorUserId().equals(event.ownerUserId())) return;

		User owner = userRepository.findById(event.ownerUserId())
				.orElseThrow(() -> new IllegalArgumentException("owner user not found: " + event.ownerUserId()));

		String actorName = userRepository.findById(event.actorUserId())
				.map(User::getName)
				.orElse("누군가");

		String tripTitle = tripRepository.findById(event.tripId())
				.map(t -> t.getTitle())
				.orElse("내 게시글");

		Notification n = Notification.tripCommentCreated(owner, actorName, tripTitle, event.tripId(), event.commentId());

		Notification saved = notificationRepository.saveAndFlush(n);
		log.info("[Notif] saved notificationId={}", saved.getNotificationId());
	}
}
