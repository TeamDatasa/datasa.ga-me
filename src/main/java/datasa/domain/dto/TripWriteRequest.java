package datasa.domain.dto;


import datasa.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripWriteRequest {

	private User hostUser;
	
	private String title;
	
	private String description;
	
	private String region;
	
	private Integer estimatedCost; // KRW
	
	private Integer maxParticipants; // 정원
	
	private Integer durationMinutes;
	
	private String theme;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime startAt;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endAt;
	
	// kakao map
	private Double lat;
	private Double lng;
	private String address;
	private String placeName;
}
