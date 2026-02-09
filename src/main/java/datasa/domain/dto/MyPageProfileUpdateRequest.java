package datasa.domain.dto;

import datasa.domain.entity.User;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MyPageProfileUpdateRequest {
	
	private String name;
	private LocalDate birthDate;
	private User.Gender gender;
	private String countryCode;
	private String region;
	
	@Size(max = 4, message = "MBTI는 4글자여야 합니다.")
	@Pattern(
			regexp = "^(?i)([EI][SN][TF][JP])?$",
			message = "MBTI를 다시 입력해주세요."
	)
	private String mbti;
	
	private Boolean smoking;
	private Boolean drinking;
	
	@Size(max = 500)
	private String bio;
}
