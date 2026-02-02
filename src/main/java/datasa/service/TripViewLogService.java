package datasa.service;

import datasa.domain.entity.Trip;
import datasa.domain.entity.TripViewLog;
import datasa.domain.entity.User;
import datasa.repository.TripRepository;
import datasa.repository.TripViewLogRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripViewLogService {
	
	private final TripViewLogRepository tripViewLogRepository;
	private final UserRepository userRepository;
	private final TripRepository tripRepository;
	
	/** 상세 페이지 들어올 때 호출 */
	@Transactional
	public void recordView(String email, Long tripId) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		Trip trip = tripRepository.findById(tripId)
				.orElseThrow(() -> new IllegalArgumentException("Trip not found"));
		
		tripViewLogRepository.findByUser_UserIdAndTrip_TripId(user.getUserId(), tripId)
				.ifPresentOrElse(
						log -> {
							log.touch(); // viewedAt 갱신
						},
						() -> {
							tripViewLogRepository.save(TripViewLog.of(user, trip));
						}
				);
	}
}
