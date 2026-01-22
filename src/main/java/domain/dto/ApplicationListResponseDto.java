
package domain.dto;


import lombok.Getter;

@Getter
public class ApplicationListResponseDto {

    private Long applicationId;
    private Long userId;
    private String userName;
    private String status;
}