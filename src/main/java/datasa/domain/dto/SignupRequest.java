package datasa.domain.dto;

import datasa.domain.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
	
	@Email
	@NotBlank
	private String email;
	
	@NotBlank
	private String password;
	
	@NotBlank
	private String name;
	
	@NotNull
	private User.Role role;
}
