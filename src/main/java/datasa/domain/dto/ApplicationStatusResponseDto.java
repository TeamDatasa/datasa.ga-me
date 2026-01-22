package datasa.domain.dto;

import datasa.domain.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Getter;


public class ApplicationStatusResponseDto {


    private Application.Status status;

    // ⭐ 반드시 public
    public ApplicationStatusResponseDto(Application.Status status) {
        this.status = status;
    }

    public Application.Status getStatus() {
        return status;
    }
}