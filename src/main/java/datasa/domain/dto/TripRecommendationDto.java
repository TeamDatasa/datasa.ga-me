package datasa.domain.dto;

import datasa.domain.entity.Trip;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TripRecommendationDto {

    private Long tripId;
    private String title;
    private String region;
    private Double score;
    private String source; // AI / RULE / DEFAULT
//    // 신청 인원
//    private int currentApplicants;
//    private int maxParticipants;

    public static TripRecommendationDto from(
            Trip trip, Double score, String source
    ) {
        return new TripRecommendationDto(
                trip.getTripId(),
                trip.getTitle(),
                trip.getRegion(),
                score,
                source
        );
    }
}
