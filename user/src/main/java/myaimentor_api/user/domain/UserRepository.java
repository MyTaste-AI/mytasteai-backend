package myaimentor_api.user.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	/** 이메일/이름 부분일치 (대소문자 무시) — ADMIN 사용자 목록 검색용. */
	Page<User> findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(
			String email, String name, Pageable pageable);
}
