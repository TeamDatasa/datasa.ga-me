package datasa.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
		@NotNull Long userId,
		Long parentCommentId, // 대댓글이면 값 존재
		@NotBlank @Size(max = 2000) String content
) {}
