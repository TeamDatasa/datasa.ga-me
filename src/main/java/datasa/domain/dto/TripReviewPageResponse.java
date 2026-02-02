package datasa.domain.dto;

import java.util.List;

public record TripReviewPageResponse(
		TripDetailResponse trip,
		boolean canWriteReview,
		ReviewResponse myReview,
		List<ReviewResponse> reviews
) {}
