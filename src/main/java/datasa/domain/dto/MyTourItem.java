package datasa.domain.dto;

import datasa.domain.entity.Trip;

import java.time.LocalDateTime;

public record MyTourItem(
		Long tripId,
		String title,
		String region,
		LocalDateTime startAt,
		LocalDateTime endAt,
		String applicationStatus,
		Long reviewId,
		boolean canReview
) {
	
	/** 좋아요 목록용 */
	public static MyTourItem fromLiked(Trip t) {
		return new MyTourItem(
				t.getTripId(),
				t.getTitle(),
				t.getRegion(),
				t.getStartAt(),
				t.getEndAt(),
				null,
				null,
				false
		);
	}
}
