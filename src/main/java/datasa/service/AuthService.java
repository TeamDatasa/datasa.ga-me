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
	public void signup(SignupRequest req) {
		// ✅ 이메일 중복 선 체크 (500 방지)
		if (userRepository.existsByEmail(req.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
		}
		
		String encoded = passwordEncoder.encode(req.getPassword());
		
		// ✅ role null 들어와도 USER로 보정
		User.Role role = (req.getRole() == null) ? User.Role.USER : req.getRole();
		
		User user = User.create(req.getEmail(), encoded, req.getName(), role);
		
		// ✅ 추가 정보 세팅
		user.setBirthDate(req.getBirthDate());
		user.setGender(req.getGender());
		user.setCountryCode(req.getCountryCode());
		user.setRegion(req.getRegion());
		
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
		
		if (status == User.Status.DELETED) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "탈퇴한 계정입니다.");
		}
		if (status != User.Status.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비활성화된 계정입니다.");
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
	
	@Transactional
	public void withdraw(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
				));
		
		User.Status status = (user.getStatus() == null) ? User.Status.ACTIVE : user.getStatus();
		if (status != User.Status.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 탈퇴된 계정입니다.");
		}
		
		// ✅ 비식별 + DELETED 처리
		user.withdrawAnonymize();
		
		// ✅ 비밀번호 무효화(선택이지만 추천)
		user.changePassword(
				passwordEncoder.encode("DELETED-" + user.getUserId() + "-" + System.currentTimeMillis())
		);
		
		// ✅ 확실히 반영(권장)
		userRepository.save(user);
		userRepository.flush();
	}
	
}
