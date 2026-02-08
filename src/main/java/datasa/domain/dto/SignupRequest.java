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
	
	@Email
	@NotBlank
	private String email;
	
	@NotBlank
	private String password;
	
	@NotBlank
	private String name;
	
	@NotNull
	private User.Role role;
	
	@NotNull
	private LocalDate birthDate;
	
	@NotNull
	private User.Gender gender;
	
	@NotBlank
	private String countryCode;
	
	@NotBlank
	private String region;
}
