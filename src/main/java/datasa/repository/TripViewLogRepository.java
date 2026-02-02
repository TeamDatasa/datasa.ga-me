package datasa.repository;

import datasa.domain.entity.TripViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripViewLogRepository extends JpaRepository<TripViewLog, Long> {
	
	Optional<TripViewLog> findByUser_UserIdAndTrip_TripId(Long userId, Long tripId);
	
	List<TripViewLog> findTop20ByUser_UserIdOrderByViewedAtDesc(Long userId);
}
