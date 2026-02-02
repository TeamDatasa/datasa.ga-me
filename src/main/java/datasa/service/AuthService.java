package datasa.service;

import datasa.domain.dto.AuthResponse;
import datasa.dto.LoginRequest;
import datasa.domain.dto.SignupRequest;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	
	@Transactional
	public void signup(SignupRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email is already in use.");
		}
		
		String hashed = passwordEncoder.encode(request.getPassword());
		
		// request.role이 null이면 USER로
		User.Role role = (request.getRole() == null) ? User.Role.USER : request.getRole();
		
		User user = User.create(
				request.getEmail(),
				hashed,
				request.getName(),
				role
		);
		
		userRepository.save(user);
	}
	
	
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.UNAUTHORIZED, "Invalid email or password."
				));
		
		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
		}
		
		// status가 null이면 ACTIVE로 간주하기
		User.Status status = (user.getStatus() == null) ? User.Status.ACTIVE : user.getStatus();
		if (status != User.Status.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inactive user.");
		}
		
		// role이 null이면 USER로 보정하기
		User.Role role = (user.getRole() == null) ? User.Role.USER : user.getRole();
		
		String accessToken = jwtTokenProvider.createToken(user.getEmail(), role);
		
		return new AuthResponse(
				accessToken,
				user.getUserId(),
				user.getEmail(),
				role
		);
	}

	
}
