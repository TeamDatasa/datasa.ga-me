package datasa.dto;

import datasa.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationStatusResponseDto {
	private Application.Status status;
}