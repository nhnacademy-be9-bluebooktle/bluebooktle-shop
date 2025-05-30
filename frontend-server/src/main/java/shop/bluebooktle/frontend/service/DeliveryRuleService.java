package shop.bluebooktle.frontend.service;

import java.util.List;

import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;

public interface DeliveryRuleService {
	List<DeliveryRuleResponse> getDeliveryRules();
}
