package shop.bluebooktle.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.common.entity.auth.MembershipLevel;

public interface MembershipLevelRepository extends JpaRepository<MembershipLevel, Long> {
	Optional<MembershipLevel> findByName(String name);
}