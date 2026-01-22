package datasa.service;

import datasa.dto.AuthResponse;
import datasa.dto.LoginRequest;
import datasa.dto.SignupRequest;
import datasa.entity.User;
import datasa.repository.UserRepository;
import datasa.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
				request.getRole() // USER / HOST
		);
		
		userRepository.save(user);
	}
	
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
		
		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new IllegalArgumentException("Invalid email or password.");
		}
		
		String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
		
		return new AuthResponse(token, user.getUserId(), user.getEmail(), user.getRole());
	}
}
