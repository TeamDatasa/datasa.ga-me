package datasa.repository;

import datasa.domain.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
	
	// 중복 신청 체크
	boolean existsByTrip_TripIdAndUser_UserId(Long tripId, Long userId);
	
	// 승인 인원 수
	long countByTrip_TripIdAndStatus(Long tripId, Application.Status status);


    // 내 신청 상태 조회
    Optional<Application> findByTrip_TripIdAndUser_UserId(
            Long tripId,
            Long userId
    );

	// (다음 단계: 승인/거절용)
	Optional<Application> findById(Long applicationId);

    List<Application> findByTrip_TripId(Long tripId);
	
	// 유저 확인용
	
	boolean existsByUser_UserIdAndTrip_TripIdAndStatus(
			Long userId, Long tripId, Application.Status status
	);
	
	List<Application> findTop20ByUser_UserIdOrderByApplicationIdDesc(Long userId);
	
	List<Application> findByUser_UserIdAndStatusOrderByApplicationIdDesc(
			Long userId, Application.Status status
	);
	
	long countByUser_UserIdAndStatus(Long userId, Application.Status status);
	
}

