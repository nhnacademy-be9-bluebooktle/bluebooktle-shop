package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleUpdateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;

public interface AdminDeliveryRuleService {

	DeliveryRuleResponse getRuleById(Long id);

	PaginationData<DeliveryRuleResponse> getAllRules(int page, int size);

	PaginationData<DeliveryRuleResponse> getActiveRules(int page, int size);

	Long createRule(DeliveryRuleCreateRequest request);

	void updateRule(Long id, DeliveryRuleUpdateRequest request);

	void deleteRule(Long id);
}
