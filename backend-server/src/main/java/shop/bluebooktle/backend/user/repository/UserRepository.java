package shop.bluebooktle.backend.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.entity.auth.User;

public interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {

	Optional<User> findByLoginId(String loginId);

	Optional<User> findByEmail(String email);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	List<User> findByStatus(UserStatus status);

	List<User> findByStatusAndLastLoginAtBefore(UserStatus status, LocalDateTime lastLoginTime);
}