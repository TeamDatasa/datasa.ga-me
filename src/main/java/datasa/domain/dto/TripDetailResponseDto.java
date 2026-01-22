package datasa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TripDetailResponseDto {

    private Long tripId;
    private String title;
    private String description;
    private String region;
    private String theme;
    private int maxParticipants;
    private int estimatedCost;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private String hostName;
    private List<String> languages;
}
