package datasa.repository;

import datasa.domain.entity.Notification;
import datasa.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	
	long countByUserAndIsReadFalse(User user);
	
	List<Notification> findByUserAndIsReadFalse(User user);
	
	List<Notification> findByUserOrderByCreatedAtDesc(User user);
}
