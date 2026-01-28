package datasa.repository;

import datasa.domain.entity.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {
	Optional<EmailVerificationCode> findTopByEmailAndCodeAndUsedFalseOrderByIdDesc(String email, String code);
	void deleteByEmail(String email);
}
