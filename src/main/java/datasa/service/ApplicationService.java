package datasa.service;

import datasa.domain.dto.ApplicationCreateResponseDto;
import datasa.domain.dto.ApplicationStatusResponseDto;
import datasa.domain.entity.Application;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.ApplicationRepository;
import datasa.repository.TripRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    /**
     * U_004 여행 신청
     */
    @Transactional
    public ApplicationCreateResponseDto applyTrip(Long tripId, Long userId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("여행이 존재하지 않습니다."));

        if (trip.getStatus() != Trip.Status.OPEN) {
            throw new IllegalStateException("신청 가능한 여행이 아닙니다.");
        }

        if (applicationRepository.existsByTrip_TripIdAndUser_UserId(tripId, userId)) {
            throw new IllegalStateException("이미 신청한 여행입니다.");
        }

        long approvedCount =
                applicationRepository.countByTrip_TripIdAndStatus(
                        tripId, Application.Status.APPROVED
                );

        if (approvedCount >= trip.getMaxParticipants()) {
            throw new IllegalStateException("정원이 초과되었습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Application app = new Application();
        app.setTrip(trip);
        app.setUser(user);
        app.setStatus(Application.Status.PENDING);

        applicationRepository.save(app);

        return new ApplicationCreateResponseDto(
                app.getApplicationId(),
                tripId,
                app.getStatus().name()
        );
    }

    /**
     * U_005 내 신청 상태 조회
     */
    public ApplicationStatusResponseDto getMyApplicationStatus(Long tripId, Long userId) {

        ApplicationStatusResponseDto dto =
                applicationRepository.findStatus(tripId, userId);

        // 신청 안 한 경우
        if (dto == null) {
            return new ApplicationStatusResponseDto(null);
        }

        return dto;
    }
}
