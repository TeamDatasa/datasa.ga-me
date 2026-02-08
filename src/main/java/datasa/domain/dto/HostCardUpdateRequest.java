package datasa.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HostCardUpdateRequest {
	@JsonProperty("isPublic")
	private Boolean isPublic;
}
