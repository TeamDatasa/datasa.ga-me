package datasa.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
		@NotNull Long userId,
		@NotBlank @Size(max = 2000) String content
) {}