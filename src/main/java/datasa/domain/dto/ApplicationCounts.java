package datasa.domain.dto;

public record ApplicationCounts (
		long pending,
		long approved,
		long rejected
){}
