package datasa.repository;

import datasa.domain.entity.TripLike;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripLikeRepository extends JpaRepository<TripLike, Long> {
	
	boolean existsByTripAndUser(Trip trip, User user);
	
	void deleteByTripAndUser(Trip trip, User user);
	
	long countByTrip(Trip trip);


	@Modifying
	@Query("delete from TripLike tl where tl.trip.tripId = :tripId")
	void deleteAllByTripId(@Param("tripId") Long tripId);

	// tripIds별 좋아요 카운트 일괄 조회
	@Query("""
        select tl.trip.tripId, count(tl)
        from TripLike tl
        where tl.trip.tripId in :tripIds
        group by tl.trip.tripId
    """)
	List<Object[]> countByTripIds(@Param("tripIds") List<Long> tripIds);
	
	// 특정 유저가 좋아요한 tripIds만 일괄 조회
	@Query("""
        select tl.trip.tripId
        from TripLike tl
        where tl.user.userId = :userId
          and tl.trip.tripId in :tripIds
    """)
	List<Long> findLikedTripIds(@Param("userId") Long userId,
								@Param("tripIds") List<Long> tripIds);
	
	List<TripLike> findTop20ByUser_UserIdOrderByCreatedAtDesc(Long userId);


}