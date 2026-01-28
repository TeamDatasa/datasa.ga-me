package datasa.domain.dto;

public record ReviewResponse(
		Long reviewId,
		Long tripId,
		int rating,
		String content,
		String createdAt,
		String updatedAt
) {}
