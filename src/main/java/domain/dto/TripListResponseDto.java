
package domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TripListResponseDto {

    private Long tripId;
    private String title;
    private String region;
    private String theme;
    private int maxParticipants;
    private long approvedCount;
}