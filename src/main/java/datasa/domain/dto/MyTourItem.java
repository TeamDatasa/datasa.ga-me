package datasa.domain.dto;

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
) { }
