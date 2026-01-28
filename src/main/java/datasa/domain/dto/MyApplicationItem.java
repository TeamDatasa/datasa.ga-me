package datasa.domain.dto;

public record MyApplicationItem (
		Long applicationId,
		Long tripId,
		String tripTitle,
		String status,
		String message,
		String appliedAt
) {}
