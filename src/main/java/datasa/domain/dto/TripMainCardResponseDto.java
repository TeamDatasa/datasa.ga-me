package datasa.domain.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class TripMainCardResponseDto {
	
	private final Long tripId;
	private final String title;
	private final String region;
	private final String theme;
	private final Integer maxParticipants;
	private final Long approvedCount;
	private final List<String> languageCodes;
	
	public TripMainCardResponseDto(
			Long tripId,
			String title,
			String region,
			String theme,
			Integer maxParticipants,
			Long approvedCount,
			List<String> languageCodes
	) {
		this.tripId = tripId;
		this.title = title;
		this.region = region;
		this.theme = theme;
		this.maxParticipants = maxParticipants;
		this.approvedCount = approvedCount;
		this.languageCodes = languageCodes;
	}
}