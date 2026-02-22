package datasa.domain.dto;


import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
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
	private boolean hostDeleted;
	
	// Trip 기본 정보
	private String title;
	private String description;
	private String region;
	private String theme;
	private Integer estimatedCost;
	private Integer maxParticipants;
	private Integer durationMinutes;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Trip.Status status;
	
	
	// 메타
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	
	private long likeCount;
	private boolean likedByMe;


	public static TripListResponse from(Trip trip) {
		boolean hostDeleted = trip.getHostUser() != null
				&& trip.getHostUser().getStatus() == User.Status.DELETED;

		return TripListResponse.builder()
				.tripId(trip.getTripId())
				.hostUserId(trip.getHostUser().getUserId())
				.hostName(trip.getHostUser().getName())
				.hostDeleted(hostDeleted)
				.title(trip.getTitle())
				.description(trip.getDescription())
				.region(trip.getRegion())
				.theme(trip.getTheme())
				.estimatedCost(trip.getEstimatedCost())
				.maxParticipants(trip.getMaxParticipants())
				.durationMinutes(trip.getDurationMinutes())
				.startAt(trip.getStartAt())
				.endAt(trip.getEndAt())
				.status(trip.getStatus())
				.createdAt(trip.getCreatedAt())
				.updatedAt(trip.getUpdatedAt())
				.likeCount(0L)
				.likedByMe(false)
				.build();
	}
}