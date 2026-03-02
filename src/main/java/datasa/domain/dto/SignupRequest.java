package datasa.domain.dto;

import datasa.domain.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SignupRequest {
	
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	@NotBlank(message = "이메일을 입력해주세요.")
	private String email;
	
	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;
	
	@NotBlank(message = "닉네임을 입력해주세요.")
	private String name;
	
	@NotNull(message = "생년월일을 입력해주세요.")
	private LocalDate birthDate;
	
	@NotNull(message = "성별을 선택해주세요.")
	private User.Gender gender;
	
	@NotBlank(message = "국가를 선택해주세요.")
	private String countryCode;
	
	@NotBlank(message = "지역을 입력/선택해주세요.")
	private String region;
}
