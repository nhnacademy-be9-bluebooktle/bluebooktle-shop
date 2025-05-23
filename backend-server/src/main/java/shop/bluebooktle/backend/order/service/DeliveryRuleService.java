package shop.bluebooktle.backend.order.service;

import java.math.BigDecimal;
import java.util.List;

import shop.bluebooktle.backend.order.entity.DeliveryRule;

public interface DeliveryRuleService {

	// "기본 정책" 1개만 존재 (30,000원 이상 → 무료 배송, 미만 → 5,000원 배송비 부과)
	DeliveryRule getDefaultRule();

	// 정책 조회
	DeliveryRule getRule(Long id);

	// 관리자 용 정책 추가 기능
	DeliveryRule createPolicy(String name, BigDecimal price, BigDecimal deliveryFee);

	// 정책 전체 조회
	List<DeliveryRule> getAll();

	// 정책 삭제
	void deletePolicy(Long id);
}
