package datasa.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewForm {
	@NotNull
	private Long tripId;
	
	private Long reviewId;
	
	@Min(1) @Max(5)
	private int rating = 5;
	
	@NotBlank
	private String content;
}
