package datasa.domain.dto;

import datasa.domain.entity.Application;
import java.time.LocalDateTime;

public record HostApplicationItem(
		Long applicationId,
		Long userId,
		String userName,
		String status,
		String message,
		LocalDateTime createdAt
) {
	public static HostApplicationItem from(Application a) {
		return new HostApplicationItem(
				a.getApplicationId(),
				a.getUser().getUserId(),
				a.getUser().getName(),
				a.getStatus().name(),
				a.getMessage(),
				a.getCreatedAt()
		);
	}
}
