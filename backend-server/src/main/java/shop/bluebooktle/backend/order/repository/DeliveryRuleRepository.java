package shop.bluebooktle.backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.order.entity.DeliveryRule;

public interface DeliveryRuleRepository extends JpaRepository<DeliveryRule, Long> {
}
