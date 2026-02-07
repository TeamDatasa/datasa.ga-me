package datasa.repository;

import datasa.domain.dto.TripDetailResponseDto;
import datasa.domain.dto.TripListResponseDto;
import datasa.domain.entity.Trip;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

	/**
     *  =========================
     * U_001 여행 목록 (최신순)
     * ========================= */
    @Query("""
    select new datasa.domain.dto.TripListResponseDto(
        t.tripId,
        t.title,
        t.region,
        t.theme,
        t.maxParticipants,
        (
            select count(a)
            from Application a
            where a.trip = t
              and a.status = datasa.domain.entity.Application.Status.APPROVED
        )
    )
    from Trip t
    where t.status = datasa.domain.entity.Trip.Status.OPEN
    order by t.createdAt desc
    """)
    Page<TripListResponseDto> findLatestTrips(Pageable pageable);


    /** =========================
     * U_001 여행 목록 (인기순)
     * ========================= */
    @Query("""
    select new datasa.domain.dto.TripListResponseDto(
        t.tripId,
        t.title,
        t.region,
        t.theme,
        t.maxParticipants,
        (
            select count(a)
            from Application a
            where a.trip = t
              and a.status = datasa.domain.entity.Application.Status.APPROVED
        )
    )
    from Trip t
    where t.status = datasa.domain.entity.Trip.Status.OPEN
    order by (
        select count(a2)
        from Application a2
        where a2.trip = t
          and a2.status = datasa.domain.entity.Application.Status.APPROVED
    ) desc
    """)
    Page<TripListResponseDto> findPopularTrips(Pageable pageable);


    /** =========================
     * U_002 여행 검색 (최신순)
     * ========================= */
    @Query("""
    select distinct new datasa.domain.dto.TripListResponseDto(
        t.tripId,
        t.title,
        t.region,
        t.theme,
        t.maxParticipants,
         (
            select count(a)
            from Application a
            where a.trip = t
              and a.status = datasa.domain.entity.Application.Status.APPROVED
        )
    )
    from Trip t
    left join TripLanguage tl on tl.trip = t
    where t.status = datasa.domain.entity.Trip.Status.OPEN
      and (:region is null or t.region = :region)
      and (:theme is null or t.theme = :theme)
      and (:languages is null or tl.languageCode in :languages)
    order by t.createdAt desc
    """)
    Page<TripListResponseDto> searchByFilters(
            @Param("region") String region,
            @Param("theme") String theme,
            @Param("languages") List<String> languages,
            Pageable pageable
    );


    /** =========================
     * U_002 여행 검색 (인기순)
     * ========================= */
    @Query("""
    select distinct new datasa.domain.dto.TripListResponseDto(
        t.tripId,
        t.title,
        t.region,
        t.theme,
        t.maxParticipants,
          (
            select count(a)
            from Application a
            where a.trip = t
              and a.status = datasa.domain.entity.Application.Status.APPROVED
        )
    )
    from Trip t
    left join TripLanguage tl on tl.trip = t
    where t.status = datasa.domain.entity.Trip.Status.OPEN
      and (:region is null or t.region = :region)
      and (:theme is null or t.theme = :theme)
      and (:languages is null or tl.languageCode in :languages)
    order by (
        select count(a2)
        from Application a2
        where a2.trip = t
          and a2.status = datasa.domain.entity.Application.Status.APPROVED
    ) desc
    """)
    Page<TripListResponseDto> searchByFiltersPopular(
            @Param("region") String region,
            @Param("theme") String theme,
            @Param("languages") List<String> languages,
            Pageable pageable
    );


    //로그인 안했을때는 최신여행 8개 추천

    @Query("""
    select t
    from Trip t
    where t.status = datasa.domain.entity.Trip.Status.OPEN
    order by t.createdAt desc
    """)
    List<Trip> findTop8Default(Pageable pageable);


    // ai결과 없을시 같은지역 및 인기순 추천
    /* =====================================================
     * Rule-based 추천 (같은 지역 + 인기순)
     * ===================================================== */
    @Query("""
    select t
    from Trip t
    where t.status = datasa.domain.entity.Trip.Status.OPEN
      and t.region = (
          select u.region
          from User u
          where u.userId = :userId
      )
    order by (
        select count(a)
        from Application a
        where a.trip = t
          and a.status = datasa.domain.entity.Application.Status.APPROVED
    ) desc
    """)
    List<Trip> findRuleBased(
            @Param("userId") Long userId,
            Pageable pageable
    );


//	@Query("""
//	select new datasa.domain.dto.TripListResponseDto(
//    t.tripId,
//    t.title,
//    t.region,
//    t.theme,
//    t.maxParticipants,
//    count(a)
//)
//from Trip t
//left join Application a
//    on a.trip = t
//    and a.status = 'APPROVED'
//where t.hostUser.userId = :hostUserId
//group by t
//order by t.createdAt desc
//""")
//	List<TripListResponseDto> findMyTrips(@Param("hostUserId") Long hostUserId);


    /* =====================================================
     * 호스트가 만든 여행 목록
     * ===================================================== */
    @Query("""
    select new datasa.domain.dto.TripListResponseDto(
        t.tripId,
        t.title,
        t.region,
        t.theme,
        t.maxParticipants,
        (
            select count(a)
            from Application a
            where a.trip = t
              and a.status = datasa.domain.entity.Application.Status.APPROVED
        )
    )
    from Trip t
    where t.hostUser.userId = :hostUserId
    order by t.createdAt desc
    """)
    List<TripListResponseDto> findMyTrips(@Param("hostUserId") Long hostUserId);


    @Query("""
select new datasa.domain.dto.TripListResponseDto(
    t.tripId,
    t.title,
    t.region,
    t.theme,
    t.maxParticipants,
    (
      select count(a)
      from Application a
      where a.trip = t
        and a.status = datasa.domain.entity.Application.Status.APPROVED
    )
)
from Trip t
where t.status = datasa.domain.entity.Trip.Status.OPEN
  and (:region is null or t.region = :region)
  and (:theme is null or t.theme = :theme)
order by t.createdAt desc
""")
    Page<TripListResponseDto> searchByFiltersNoLang(
            @Param("region") String region,
            @Param("theme") String theme,
            Pageable pageable
    );

    @Query("""
select new datasa.domain.dto.TripListResponseDto(
    t.tripId,
    t.title,
    t.region,
    t.theme,
    t.maxParticipants,
    (
      select count(a)
      from Application a
      where a.trip = t
        and a.status = datasa.domain.entity.Application.Status.APPROVED
    )
)
from Trip t
where t.status = datasa.domain.entity.Trip.Status.OPEN
  and (:region is null or t.region = :region)
  and (:theme is null or t.theme = :theme)
order by (
    select count(a2)
    from Application a2
    where a2.trip = t
      and a2.status = datasa.domain.entity.Application.Status.APPROVED
) desc
""")
    Page<TripListResponseDto> searchByFiltersPopularNoLang(
            @Param("region") String region,
            @Param("theme") String theme,
            Pageable pageable
    );





    // 동시 승인 방지
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Trip t where t.tripId = :tripId")
    Optional<Trip> findByIdForUpdate(@Param("tripId") Long tripId);

    List<Trip> findByHostUser_UserIdOrderByCreatedAtDesc(Long hostUserId);
   List<Trip> findByHostUser_UserIdOrderByTripIdDesc(Long hostUserId);

   List<Trip> findByHostUser_UserId(Long hostUserId);



}
