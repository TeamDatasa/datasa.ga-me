package datasa.service;

import datasa.domain.entity.EmailVerificationCode;
import datasa.domain.entity.User;
import datasa.repository.EmailVerificationCodeRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
	
	private final EmailVerificationCodeRepository codeRepository;
	private final UserRepository userRepository;
	private final MailService mailService;
	
	private static final int EXPIRE_MIN = 5;
	private static final SecureRandom random = new SecureRandom();
	
	private String generate6Digits() {
		int n = random.nextInt(900000) + 100000;
		return String.valueOf(n);
	}
	
	@Transactional
	public void sendCode(String email) {
		codeRepository.deleteByEmail(email);
		
		String code = generate6Digits();
		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRE_MIN);
		
		codeRepository.save(EmailVerificationCode.create(email, code, expiresAt));
		mailService.sendEmailVerificationCodeMail(email, code);
	}
	
	@Transactional
	public void verifyCode(String email, String code) {
		EmailVerificationCode evc = codeRepository
				.findTopByEmailAndCodeAndUsedFalseOrderByIdDesc(email, code)
				.orElseThrow(() -> new IllegalArgumentException("인증번호가 올바르지 않습니다."));
		
		if (evc.isExpired()) throw new IllegalArgumentException("인증번호가 만료되었습니다.");
		
		evc.markUsed();
		
		User user = userRepository.findByEmail(email).orElse(null);
		if (user != null) user.verifyEmail();
		
		codeRepository.save(evc);
		if (user != null) userRepository.save(user);
	}
}
