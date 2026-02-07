package datasa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class TripListResponseDto {

    private Long tripId;
    private String title;
    private String region;
    private String theme;
    private Integer maxParticipants;
    private Long approvedCount;

    public TripListResponseDto(
            Long tripId,
            String title,
            String region,
            String theme,
            Integer maxParticipants,
            Long approvedCount
    ) {
        this.tripId = tripId;
        this.title = title;
        this.region = region;
        this.theme = theme;
        this.maxParticipants = maxParticipants;
        this.approvedCount = approvedCount;
    }
}