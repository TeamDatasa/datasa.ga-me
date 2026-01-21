package datasa.repository;


import datasa.entity.User;
import datasa.entity.UserLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
}

