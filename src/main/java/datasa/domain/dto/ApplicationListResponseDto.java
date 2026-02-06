package datasa.domain.dto;

import datasa.domain.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApplicationListResponseDto {

    private Long applicationId;
    private Long userId;
    private String userName;
    private Application.Status status;
    private LocalDateTime createdAt;

    public static ApplicationListResponseDto from(Application app) {
        return new ApplicationListResponseDto(
                app.getApplicationId(),
                app.getUser().getUserId(),
                app.getUser().getName(),
                app.getStatus(),
                app.getCreatedAt()
        );

    }
}