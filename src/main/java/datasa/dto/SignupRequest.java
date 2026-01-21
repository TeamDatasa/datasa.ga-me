package datasa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
	
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String email;
	
	@NotBlank(message = "비밀번호는 필수입니다.")
	@Pattern(
			regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,20}$",
			message = "비밀번호는 8~20자, 영문+숫자 조합이어야 합니다"
	)
	private String password;
	
	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 50, message = "이름은 50자 이하여야 합니다")
	private String name;
}
