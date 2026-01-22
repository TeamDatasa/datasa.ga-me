package datasa.service;

import datasa.domain.dto.TripDetailResponseDto;
import datasa.domain.dto.TripListResponseDto;
import datasa.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    /**
     * U_001 여행 목록 조회
     * - latest (기본): 최신순
     * - popular: 인기순
     */
    public Page<TripListResponseDto> getTripList(
            String order,
            Pageable pageable
    ) {
        if ("popular".equalsIgnoreCase(order)) {
            return tripRepository.findPopularTrips(pageable);
        }
        return tripRepository.findLatestTrips(pageable);
    }

    /**
     * U_002 여행 검색
     * - 언어(복수) / 지역 / 테마
     * - latest / popular 지원
     */
    public Page<TripListResponseDto> searchTrips(
            List<String> languages,
            String region,
            String theme,
            String order,
            Pageable pageable
    ) {
        if ("popular".equalsIgnoreCase(order)) {
            return tripRepository.searchByFiltersPopular(
                    region, theme, languages, pageable
            );
        }

        return tripRepository.searchByFilters(
                region, theme, languages, pageable
        );
    }

    /**
     * U_003 여행 상세 조회
     */
    public TripDetailResponseDto getTripDetail(Long tripId) {
        return tripRepository.findTripDetail(tripId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 여행입니다.")
                );
    }
}
