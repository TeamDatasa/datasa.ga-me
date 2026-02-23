package datasa.service;

import datasa.domain.entity.Application;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.ApplicationRepository;
import datasa.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TripStatusScheduler {
	
	private final TripRepository tripRepository;
	private final ApplicationRepository applicationRepository;
	
	/**
	 * 1분마다 상태 재계산
	 * - 트립 수가 많아지면 후보군 조회 쿼리로 최적화 가능
	 */
	@Scheduled(fixedDelay = 60_000)
	@Transactional
	public void refreshTripStatus() {
		LocalDateTime now = LocalDateTime.now();
		
		List<Trip> trips = tripRepository.findAll();
		
		for (Trip trip : trips) {
			Trip.Status desired = computeDesiredStatus(trip, now);
			
			if (trip.getStatus() != desired) {
				trip.setStatus(desired);
			}
		}
	}
	
	private Trip.Status computeDesiredStatus(Trip trip, LocalDateTime now) {
		LocalDateTime startAt = trip.getStartAt();
		LocalDateTime endAt = trip.getEndAt();
		
		// 1) 시작 전 호스트 탈퇴 -> DRAFT
		User host = trip.getHostUser();
		boolean hostDeleted = (host != null && host.getStatus() == User.Status.DELETED);
		
		if (startAt != null && now.isBefore(startAt) && hostDeleted) {
			return Trip.Status.DRAFT;
		}
		
		// 2) 종료 시각 이후 -> FINISHED
		if (endAt != null && !now.isBefore(endAt)) {
			return Trip.Status.FINISHED;
		}
		
		// 3) 시작 시각 이후(종료 전) -> IN_PROGRESS
		if (startAt != null && !now.isBefore(startAt)) {
			return Trip.Status.IN_PROGRESS;
		}
		
		// 4) 시작 24시간 전 도달 또는 정원 마감 -> CLOSED
		boolean timeClosed = false;
		if (startAt != null) {
			LocalDateTime lockAt = startAt.minusHours(24);
			timeClosed = !now.isBefore(lockAt);
		}
		
		long approvedGuests = applicationRepository.countByTrip_TripIdAndStatus(
				trip.getTripId(), Application.Status.APPROVED
		);
		
		long totalParticipants = approvedGuests + 1; // 호스트 포함
		boolean capacityClosed = trip.getMaxParticipants() != null && totalParticipants >= trip.getMaxParticipants();
		
		if (timeClosed || capacityClosed) {
			return Trip.Status.CLOSED;
		}
		
		// 5) 나머지 -> OPEN
		return Trip.Status.OPEN;
	}
}