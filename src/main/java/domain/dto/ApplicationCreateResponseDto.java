
package domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationCreateResponseDto {

    private Long applicationId;
    private Long tripId;
    private String status;   // PENDING
}
