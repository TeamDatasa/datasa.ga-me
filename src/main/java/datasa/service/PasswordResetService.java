package datasa.service;

import datasa.domain.dto.PasswordResetConfirmRequest;
import datasa.domain.entity.PasswordResetToken;
import datasa.domain.entity.User;
import datasa.repository.PasswordResetTokenRepository;
import datasa.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
	
	private final MailService mailService;
	private final UserRepository userRepository;
	private final PasswordResetTokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Value("${app.base-url:http://localhost:8080}")
	private String baseUrl;
	
	private static final int EXPIRE_MIN = 30;
	
	@Transactional
	public void requestReset(String email){
		
		User user = userRepository.findByEmail(email).orElse(null);
		if (user == null) return;
		if (user.getStatus() != User.Status.ACTIVE) return;
		
		String token = UUID.randomUUID().toString().replace("-", "");
		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRE_MIN);
		
		tokenRepository.save(PasswordResetToken.create(user, token, expiresAt));
		
		String link = baseUrl + "/auth/reset-password?token=" + token;
		
		mailService.sendPasswordResetMail(email, link, expiresAt.toString());
		
	}
	@Transactional
	public void confirmReset(PasswordResetConfirmRequest req) {
		PasswordResetToken prt = tokenRepository.findByToken(req.getToken())
				.orElseThrow(() -> new IllegalArgumentException("Invalid reset link"));
		
		if (prt.isExpired()) throw new IllegalArgumentException("Reset link expired");
		if (prt.isUsed()) throw new IllegalArgumentException("Reset link already used");
		
		User user = prt.getUser();
		if (user.getStatus() != User.Status.ACTIVE) throw new IllegalArgumentException("Inactive user");
		
		user.changePassword(passwordEncoder.encode(req.getNewPassword()));
		prt.markUsed();
		
		userRepository.save(user);
		tokenRepository.save(prt);
	}
	
	
}
