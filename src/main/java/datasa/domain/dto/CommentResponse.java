package datasa.domain.dto;

import java.time.LocalDateTime;

public record CommentResponse(
		Long commentId,
		Long tripId,
		Long userId,
		String userName,
		Long parentCommentId,
		String content,
		String status,
		long likeCount,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {}
