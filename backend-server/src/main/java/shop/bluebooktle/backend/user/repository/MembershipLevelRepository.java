package shop.bluebooktle.backend.user.repository; // 실제 프로젝트 구조에 맞게 패키지명을 조정해주세요.

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.user.entity.MembershipLevel;

public interface MembershipLevelRepository extends JpaRepository<MembershipLevel, Long> {

	Optional<MembershipLevel> findByName(String name);

	List<MembershipLevel> findByMinNetSpentLessThanEqualAndMaxNetSpentGreaterThanEqual(BigDecimal netSpent);

}