package datasa.dto;

import datasa.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
	private String accessToken;
	private Long userId;
	private String email;
	private User.Role role;
}
