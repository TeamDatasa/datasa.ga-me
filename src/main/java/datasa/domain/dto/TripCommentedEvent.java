package datasa.domain.dto;

public record TripCommentedEvent(
		Long tripId,
		Long commentId,
		Long actorUserId,
		Long ownerUserId
) {
}
