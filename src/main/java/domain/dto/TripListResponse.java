package domain.dto;

import datasa.entity.Trip;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TripListResponse {
	
	private Long tripId;
	
	// 호스트 정보(필요 최소)
	private Long hostUserId;
	private String hostName;
	
	// Trip 기본 정보
	private String title;
	private String description;
	private Integer estimatedCost;
	private Integer maxParticipants;
	private Integer durationMinutes;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Trip.Status status;
	
	// 메타
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	public static TripListResponse from(Trip trip) {
		return TripListResponse.builder()
				.tripId(trip.getTripId())
				.hostUserId(trip.getHostUser().getUserId())
				.hostName(trip.getHostUser().getName())
				.title(trip.getTitle())
				.description(trip.getDescription())
				.estimatedCost(trip.getEstimatedCost())
				.maxParticipants(trip.getMaxParticipants())
				.durationMinutes(trip.getDurationMinutes())
				.startAt(trip.getStartAt())
				.endAt(trip.getEndAt())
				.status(trip.getStatus())
				.createdAt(trip.getCreatedAt())
				.updatedAt(trip.getUpdatedAt())
				.build();
	}
}
