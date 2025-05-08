package shop.bluebooktle.backend.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.entity.auth.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByLoginId(String loginId);

	Optional<User> findByEmail(String email);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	List<User> findByStatus(UserStatus status);

	// 특정 상태이고 마지막 로그인 시간이 특정 시간 이전인 사용자 목록을 조회합니다.
	// (예: 휴면 계정 조회)
	List<User> findByStatusAndLastLoginAtBefore(UserStatus status, LocalDateTime lastLoginTime);
}