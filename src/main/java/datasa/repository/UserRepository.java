package datasa.repository;

import datasa.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	
	boolean existsByEmail(String email);
	
	Optional<User> findByEmail(String email);
	
	// 탈퇴(DELETED) 제외하고 존재하는지
	boolean existsByEmailAndStatusNot(String email, User.Status status);
	
	// (옵션) 탈퇴 제외 조회
	Optional<User> findByEmailAndStatusNot(String email, User.Status status);
}