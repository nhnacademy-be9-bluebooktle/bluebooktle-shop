package shop.bluebooktle.backend.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.common.domain.order.Region;

public interface DeliveryRuleRepository extends JpaRepository<DeliveryRule, Long> {
	boolean existsByRuleName(String name);

	List<DeliveryRule> findAllByIsActiveTrue();

	Optional<DeliveryRule> findByRegion(Region region);

	Optional<DeliveryRule> findByRegionAndIsActiveTrue(Region region);
}
