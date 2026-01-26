package datasa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApplicationListResponseDto {

    private Long applicationId;
    private Long userId;
    private String userName;
    private String status;
    private LocalDateTime createdAt;
}