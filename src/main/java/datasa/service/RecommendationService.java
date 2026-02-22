package datasa.service;

import datasa.domain.dto.TripRecommendationDto;
import datasa.domain.entity.Trip;
import datasa.repository.ApplicationRepository;
import datasa.repository.RecommendationRepository;
import datasa.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final TripRepository tripRepository;
    private final ApplicationRepository applicationRepository;

    public List<TripRecommendationDto> recommend(Long userId) {

        // 로그인 X
        if (userId == null) {
            return getDefaultTrips();
        }

        //  AI 추천 시도
        List<Object[]> aiRows =
                recommendationRepository.findAiRecommendations(userId, 3);

        if (!aiRows.isEmpty()) {
            return aiRows.stream()
                    .map(row -> {
                        Long tripId = ((Number) row[0]).longValue();
                        Double score = ((Number) row[1]).doubleValue();
                        Trip trip = tripRepository.findById(tripId).orElseThrow();


                        return TripRecommendationDto.from(trip, score, "AI");
                    })
                    .toList();
        }

        //  fallback → 규칙 기반
        return getRuleBasedTrips(userId);
    }

    public List<TripRecommendationDto> getDefaultTrips() {
        return tripRepository
                .findTop8Default(PageRequest.of(0, 3))
                .stream()
                .map(t -> TripRecommendationDto.from(t, null, "DEFAULT"))
                .toList();
    }

    public List<TripRecommendationDto> getRuleBasedTrips(Long userId) {
        return tripRepository
                .findRuleBased(userId, PageRequest.of(0, 3))
                .stream()
                .map(t -> TripRecommendationDto.from(t, null, "RULE"))
                .toList();
    }
}

