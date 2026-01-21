package datasa.repository;

import datasa.dto.ApplicationStatusResponseDto;
import datasa.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 중복 신청 체크
    boolean existsByTrip_TripIdAndUser_UserId(Long tripId, Long userId);

    // 승인 인원 수
    long countByTrip_TripIdAndStatus(Long tripId, Application.Status status);

    // 신청 상태 조회 (DTO)
    @Query("""
        select new datasa.dto.ApplicationStatusResponseDto(a.status)
        from Application a
        where a.trip.tripId = :tripId
          and a.user.userId = :userId
    """)
    ApplicationStatusResponseDto findStatus(
            @Param("tripId") Long tripId,
            @Param("userId") Long userId
    );

    // (다음 단계: 승인/거절용)
    Optional<Application> findById(Long applicationId);
}

