package datasa.domain.dto;


import datasa.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

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
	
	// write form에서 입력된 일정을 다중값으로 받기
	// schedulePlaces[0].placeId 형태로 바인딩
	private List<TripWriteSchedulePlaceRequest> schedulePlaces;
}
