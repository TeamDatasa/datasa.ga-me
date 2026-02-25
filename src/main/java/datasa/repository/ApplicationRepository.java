package datasa.repository;

import datasa.domain.entity.Application;
import datasa.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 중복 신청 체크
    boolean existsByTrip_TripIdAndUser_UserId(Long tripId, Long userId);

    // 승인 여부 체크(채팅 입장/권한 검증에 사용)
    boolean existsByTrip_TripIdAndUser_UserIdAndStatus(
            Long tripId,
            Long userId,
            Application.Status status
    );

    // 승인 인원 수(정원 체크에 사용)
    long countByTrip_TripIdAndStatus(Long tripId, Application.Status status);

    // 내 신청 상태 조회
    Optional<Application> findByTrip_TripIdAndUser_UserId(Long tripId, Long userId);

    // 여행에 달린 신청 목록(호스트 신청관리 등)
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
	
	@Query("""
    select a
    from Application a
    join fetch a.user u
    where a.trip.tripId = :tripId
    order by a.applicationId desc
""")
	List<Application> findByTripIdWithUser(@Param("tripId") Long tripId);
	
	
	
	// 내가 승인된 여행 목록
    @Query("""
    select a.trip
    from Application a
    where a.user.userId = :userId
      and a.status = 'APPROVED'
    order by a.applicationId desc
""")
List<Trip> findApprovedTripsForChat(@Param("userId") Long userId);

	// 채팅방 목록에는 취소 승인된 여행도 남겨두기(입장/전송은 별도 권한검사로 제한)
	@Query("""
	select a.trip
	from Application a
	where a.user.userId = :userId
	  and a.status in ('APPROVED','CANCELED')
	order by a.applicationId desc
""")
	List<Trip> findTripsForChatIncludeCanceled(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Application a where a.trip.tripId = :tripId")
    void deleteByTrip_TripId(@Param("tripId") Long tripId);
	
	@Query("""
            select a.trip.tripId, count(a)
            from Application a
            where a.trip.tripId in :tripIds
              and a.status = 'APPROVED'
            group by a.trip.tripId
            """)
	List<Object[]> countApprovedByTripIds(@Param("tripIds") Collection<Long> tripIds);
}

