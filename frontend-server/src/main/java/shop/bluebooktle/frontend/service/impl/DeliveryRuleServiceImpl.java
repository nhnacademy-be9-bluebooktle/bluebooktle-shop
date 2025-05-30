package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.frontend.repository.DeliveryRuleRepository;
import shop.bluebooktle.frontend.service.DeliveryRuleService;

@Service
@RequiredArgsConstructor
public class DeliveryRuleServiceImpl implements DeliveryRuleService {

	private final DeliveryRuleRepository deliveryRuleRepository;

	@Override
	public List<DeliveryRuleResponse> getDeliveryRules() {
		return deliveryRuleRepository.getDeliveryRules();
	}
}
