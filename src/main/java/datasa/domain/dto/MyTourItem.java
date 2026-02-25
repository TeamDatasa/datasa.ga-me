package datasa.domain.dto;

import datasa.domain.entity.Trip;

import java.time.LocalDateTime;

public record MyTourItem(
		Long tripId,
		Long applicationId,
		String title,
		String region,
		LocalDateTime startAt,
		LocalDateTime endAt,
		String applicationStatus,
		Long reviewId,
		boolean canReview,
		String cancelStatus,
		boolean canCancelRequest
) {
	public static MyTourItem fromLiked(Trip t) {
		return new MyTourItem(
				t.getTripId(),
				null,
				t.getTitle(),
				t.getRegion(),
				t.getStartAt(),
				t.getEndAt(),
				null,
				null,
				false,
				null,
				false
		);
	}
}