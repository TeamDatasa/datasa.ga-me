package datasa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@Service
@RequiredArgsConstructor
public class MailService {
	
	private final JavaMailSender mailSender;
	
	@Value("${app.mail.from:no-reply@ga-me.local}")
	private String from;
	
	public void sendPasswordResetMail(String to, String resetLink, String expiresAtText) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(to);
		msg.setFrom(from);
		msg.setSubject("[Ga-me] 비밀번호 재설정");
		msg.setText("""
                아래 링크로 비밀번호를 재설정해 주세요.

                %s

                만료 시간: %s
                본인이 요청하지 않았다면 이 메일을 무시하세요.
                """.formatted(resetLink, expiresAtText));
		
		mailSender.send(msg);
	}
	
	public void sendEmailVerificationCodeMail(String to, String code) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(to);
		msg.setFrom(from);
		msg.setSubject("[Ga-me] 회원가입 이메일 인증번호");
		msg.setText("""
            아래 인증번호 6자리를 입력해 주세요.

            인증번호: %s

            (유효시간 5분)
            """.formatted(code));
		mailSender.send(msg);
	}
	
}
