package datasa.service;

import datasa.entity.Trip;
import datasa.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    /**
     * U_001 여행 목록 조회
     * - latest (기본): 최신순
     * - popular: 인기순
     */
    public Page<Trip> getTripList(
            String order,
            Pageable pageable
    ) {
        // 인기순
        if ("popular".equalsIgnoreCase(order)) {
            return tripRepository.findPopularTrips(pageable);
        }

        // 기본: 최신순
        return tripRepository.findByStatus(
                Trip.Status.OPEN,
                pageable
        );
    }

    /**
     * U_002 여행 검색 (언어별,지역별,테마별)
     * - latest / popular 지원
     */
    public Page<Trip> searchTrips(
            String language,
            String region,
            String theme,
            String order,
            Pageable pageable
    ) {
        if ("popular".equals(order)) {
            return tripRepository.searchByFiltersPopular(
                    language, region, theme, pageable
            );
        }

        return tripRepository.searchByFilters(
                language, region, theme, pageable
        );
    }
}
