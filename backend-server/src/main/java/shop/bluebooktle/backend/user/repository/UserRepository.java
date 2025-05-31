package shop.bluebooktle.backend.user.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import feign.Param;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.entity.auth.User;

public interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {

	Optional<User> findByLoginId(String loginId);

	Optional<User> findByEmail(String email);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	List<User> findByStatus(UserStatus status);

	List<User> findByStatusAndLastLoginAtBefore(UserStatus status, LocalDateTime lastLoginTime);

	@Query("SELECT u.pointBalance FROM User u WHERE u.id = :id")
	Optional<BigDecimal> findPointBalanceByLoginId(@Param("id") Long id);

	@EntityGraph(attributePaths = "addresses")
	@Query("SELECT u FROM User u WHERE u.id = :userId")
	Optional<User> findUserWithAddresses(@Param("userId") Long userId);
}