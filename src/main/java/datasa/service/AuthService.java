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

		User user = User.create(
				request.getEmail(),
				hashed,
				request.getName(),
				request.getRole()
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
		
		if (user.getStatus() != User.Status.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inactive user.");
		}
		
		String accessToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
		
		return new AuthResponse(
				accessToken,
				user.getUserId(),
				user.getEmail(),
				user.getRole()
		);
	}
	
}
