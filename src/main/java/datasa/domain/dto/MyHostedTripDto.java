package datasa.domain.dto;

import datasa.domain.entity.Trip;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyHostedTripDto {

    private Long tripId;
    private String title;
    private String region;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public static MyHostedTripDto from(Trip trip) {
        return new MyHostedTripDto(
                trip.getTripId(),
                trip.getTitle(),
                trip.getRegion(),
                trip.getStartAt(),
                trip.getEndAt()
        );
    }
}
