package datasa.repository;




import datasa.entity.Trip;
import datasa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    // U_001 최신순
    Page<Trip> findByStatus(Trip.Status status, Pageable pageable);

    // U_001 인기순
    @Query("""
        select t
        from Trip t
        left join Application a
          on a.trip = t and a.status = 'APPROVED'
        where t.status = 'OPEN'
        group by t.tripId
        order by count(a.applicationId) desc
    """)
    Page<Trip> findPopularTrips(Pageable pageable);

    // U_002 검색 - 최신순
    @Query("""
        select distinct t
        from Trip t
        left join t.hostUser u
        left join TripLanguage tl on tl.trip = t
        where t.status = 'OPEN'
          and (:language is null or tl.languageCode = :language)
          and (:region is null or u.region = :region)
          and (:theme is null or t.theme = :theme)
    """)
    Page<Trip> searchByFilters(
            @Param("language") String language,
            @Param("region") String region,
            @Param("theme") String theme,
            Pageable pageable
    );

    // U_002 검색 - 인기순
    @Query("""
    select t
    from Trip t
    left join t.hostUser u
    left join TripLanguage tl on tl.trip = t
    left join Application a
        on a.trip = t and a.status = 'APPROVED'
    where t.status = 'OPEN'
      and (:language is null or tl.languageCode = :language)
      and (:region is null or u.region = :region)
      and (:theme is null or t.theme = :theme)
    group by t
    order by count(a.applicationId) desc
""")
    Page<Trip> searchByFiltersPopular(
            @Param("language") String language,
            @Param("region") String region,
            @Param("theme") String theme,
            Pageable pageable
    );
}

