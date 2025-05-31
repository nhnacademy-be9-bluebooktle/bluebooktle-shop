package shop.bluebooktle.backend.order.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
	List<DeliveryRuleResponse> getAllByIsActive();

	void deleteRule(Long id);

	void updateRule(Long id, DeliveryRuleUpdateRequest request);
}