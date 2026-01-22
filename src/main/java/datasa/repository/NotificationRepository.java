package datasa.repository;

import datasa.domain.entity.Notification;
import datasa.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 회원 알림 전체 조회 (최신순)
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // 읽지 않은 알림
    List<Notification> findByUserAndIsReadFalse(User user);

    // 특정 참조 ID 기준 알림 조회 (trip_id 등)
    List<Notification> findByUserAndRefId(User user, Long refId);

    // 읽음 처리용
    long countByUserAndIsReadFalse(User user);
}
