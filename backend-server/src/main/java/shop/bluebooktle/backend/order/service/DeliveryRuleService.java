package shop.bluebooktle.backend.order.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleUpdateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;

public interface DeliveryRuleService {

	// 기본 정책 조회. region이 ALL인 정책을 반환
	DeliveryRuleResponse getDefaultRule();

	DeliveryRuleResponse getRule(Long id);

	Long createRule(DeliveryRuleCreateRequest request);

	Page<DeliveryRuleResponse> getAll(Pageable pageable);

	// 활성화된 정책 조회
	Page<DeliveryRuleResponse> getAllByIsActive(Pageable pageable);

	void deleteRule(Long id);

	// 결제 금액과 지역을 기반으로 실제 적용될 배송비 계산
	BigDecimal calculateDeliveryFee(BigDecimal orderAmount, Region region);

	void updateRule(Long id, DeliveryRuleUpdateRequest request);
}