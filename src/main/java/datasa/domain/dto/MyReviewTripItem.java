package datasa.domain.dto;

import java.time.LocalDateTime;

public record MyReviewTripItem(
		Long tripId,
		String title,
		String region,
		LocalDateTime startAt,
		LocalDateTime endAt,
		boolean ended,
		boolean canWrite,
		boolean hasReview,
		Long reviewId
) {}
