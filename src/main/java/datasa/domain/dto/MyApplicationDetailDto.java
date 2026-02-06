package datasa.domain.dto;


import datasa.domain.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyApplicationDetailDto {

    private Long applicationId;
    private Long tripId;
    private String tripTitle;
    private Application.Status status;

    public static MyApplicationDetailDto from(Application app) {
        return new MyApplicationDetailDto(
                app.getApplicationId(),
                app.getTrip().getTripId(),
                app.getTrip().getTitle(),
                app.getStatus()
        );
    }
}
