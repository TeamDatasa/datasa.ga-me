package domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationStatusResponseDto {

    private String status; // PENDING / APPROVED / REJECTED
}