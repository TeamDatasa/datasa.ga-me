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
	
	@Size(max = 4)
	private String mbti;
	
	private Boolean smoking;
	private Boolean drinking;
	
	@Size(max = 500)
	private String bio;
}
