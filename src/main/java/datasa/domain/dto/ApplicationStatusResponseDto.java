package datasa.domain.dto;

import datasa.domain.entity.Application;
import lombok.Getter;

@Getter
public class ApplicationStatusResponseDto {
	
	private final Application.Status status;
	
	public ApplicationStatusResponseDto(Application.Status status) {
		this.status = status;
	}
}
