package datasa.domain.dto;

import datasa.domain.entity.Application;
import datasa.domain.entity.Trip;

import java.time.LocalDateTime;

public record MyApplicationItem(
		Long applicationId,
		Long tripId,
		String tripTitle,
		Application.Status status,
		String message,
		LocalDateTime createdAt
) {
	public static MyApplicationItem from(Application a) {
		Trip t = a.getTrip();
		return new MyApplicationItem(
				a.getApplicationId(),
				t.getTripId(),
				t.getTitle(),
				a.getStatus(),
				a.getMessage(),
				a.getCreatedAt()
		);
	}
}
