package datasa.domain.dto;

public record RatingSummary(
		long count,
		double avg,
		int trustScore // 0~100 (avg*20)
) {
	public static RatingSummary of(long count, double avg) {
		return new RatingSummary(count, avg, (int) Math.round(avg * 20));
	}
}
