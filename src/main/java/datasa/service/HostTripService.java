package datasa.service;

import datasa.domain.dto.TripListResponse;
import datasa.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HostTripService {
	
	private final TripRepository tripRepository;
	
	@Transactional(readOnly = true)
	public List<TripListResponse> getMyTrips(Long hostUserId) {
		return tripRepository.findByHostUser_UserIdOrderByCreatedAtDesc(hostUserId)
				.stream()
				.map(TripListResponse::from)
				.toList();
	}
	
	// write/update/delete도 여기에 몰아넣기
}
