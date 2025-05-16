package shop.bluebooktle.backend.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.order.entity.DeliveryRule;

public interface DeliveryRuleRepository extends JpaRepository<DeliveryRule, Long> {

	// 정책 이름으로 조회
	Optional<DeliveryRule> findById(Long id);

	// INSERT INTO delivery_rule (name, price, delivery_fee)
	// VALUES ('기본 배송 정책', 30000.00, 5000.00);

	// 중복 정책명 확인
	boolean existsByName(String name);

}
