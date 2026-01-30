package datasa.domain.dto;

// 좋아요 이벤트
public record TripLikedEvent(
		Long tripId,
		Long actorUserId,
		Long ownerUserId
) { }
