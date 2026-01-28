package datasa.service;

import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoleService {
	
	private final UserRepository userRepository;
	
	@Transactional
	public void updateRole(String email, String roleStr) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		User.Role role = User.Role.valueOf(roleStr.trim().toUpperCase());
		user.changeRole(role);
	}
	
}
