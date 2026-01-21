package datasa.service;

import datasa.dto.SignupRequest;
import datasa.entity.User;
import datasa.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	@Transactional
	public Long signUp(SignupRequest req){
		if (userRepository.existsByEmail(req.getEmail())){
			throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
		}
		
		User user = new User(
				req.getEmail(),
				passwordEncoder.encode(req.getPassword()),
				req.getName()
		);
		
		User saved = userRepository.save(user);
		return saved.getUserId();
	}
}
