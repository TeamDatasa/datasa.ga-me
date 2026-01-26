package datasa.domain.dto;


import datasa.domain.entity.Trip;
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
public class TripDetailResponse {
	private Long tripId;
	
	private User hostUser;
	
	private String title;
	
	private String description;
	
	private Integer estimatedCost; // KRW
	
	private Integer maxParticipants; // 정원
	
	private Integer durationMinutes;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime startAt;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endAt;
	
	private Trip.Status status;
	private String theme;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Integer editLockDays;
	
	private List<TripLocationItemResponse> locations;

}
