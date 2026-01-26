package datasa.domain.dto;

import datasa.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MyPageProfileResponse {
	private String email;
	private String name;
	private LocalDate birthDate;
	private Integer age;
	
	private User.Gender gender;
	private String countryCode;
	private String region;
	private String mbti;
	
	private Boolean smoking;
	private Boolean drinking;
	private String bio;
	
	private String profileImageUrl;
	private User.Role role;
	private Boolean localVerified;
}
