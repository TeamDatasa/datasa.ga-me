package datasa.service;

import datasa.domain.dto.HostApplicationItem;
import datasa.domain.dto.TripListResponse;
import datasa.domain.entity.Application;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.ApplicationRepository;
import datasa.repository.TripRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HostTripService {
	
	private final TripRepository tripRepository;
	private final ApplicationRepository applicationRepository;
	private final UserRepository userRepository;
	
	@Transactional(readOnly = true)
	public List<TripListResponse> getMyTrips(Long hostUserId) {
		return tripRepository.findByHostUser_UserIdOrderByCreatedAtDesc(hostUserId)
				.stream()
				.map(TripListResponse::from)
				.toList();
	}
	
	// =========================
	// 신청현황 조회 (내 투어인지 검증)
	// =========================
	@Transactional(readOnly = true)
	public List<HostApplicationItem> getTripApplications(Long tripId, String hostEmail) {
		
		User host = userRepository.findByEmail(hostEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."));
		
		Trip trip = tripRepository.findByIdWithHostUser(tripId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "투어를 찾을 수 없습니다."));
		
		if (trip.getHostUser() == null || !trip.getHostUser().getUserId().equals(host.getUserId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "내 투어의 신청현황만 볼 수 있습니다.");
		}
		
		return applicationRepository.findByTripIdWithUser(tripId)
				.stream()
				.map(HostApplicationItem::from)
				.toList();
	}
	
	// =========================
	// 승인/거절 (host 권한 체크)
	// =========================
	public void approveApplication(Long tripId, Long applicationId, String hostEmail) {
		Application a = getOwnedApplication(tripId, applicationId, hostEmail);
		if (a.getStatus() != Application.Status.PENDING) return; // 이미 처리됨이면 무시
		a.approve();
	}
	
	public void rejectApplication(Long tripId, Long applicationId, String hostEmail) {
		Application a = getOwnedApplication(tripId, applicationId, hostEmail);
		if (a.getStatus() != Application.Status.PENDING) return;
		a.reject();
	}
	
	private Application getOwnedApplication(Long tripId, Long applicationId, String hostEmail) {
		
		// ✅ 내 투어인지 검증
		getTripApplications(tripId, hostEmail);
		
		Application a = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "신청을 찾을 수 없습니다."));
		
		if (!a.getTrip().getTripId().equals(tripId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
		}
		
		return a;
	}
}
