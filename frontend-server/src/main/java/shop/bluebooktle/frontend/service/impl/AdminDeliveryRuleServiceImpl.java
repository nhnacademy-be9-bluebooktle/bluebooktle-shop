package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleUpdateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.frontend.repository.AdminDeliveryRuleRepository;
import shop.bluebooktle.frontend.service.AdminDeliveryRuleService;

@Service
@RequiredArgsConstructor
public class AdminDeliveryRuleServiceImpl implements AdminDeliveryRuleService {

	private final AdminDeliveryRuleRepository repository;

	@Override
	public DeliveryRuleResponse getRuleById(Long id) {
		return repository.getById(id);
	}

	@Override
	public PaginationData<DeliveryRuleResponse> getAllRules(int page, int size) {
		return repository.getAll(page, size);
	}

	@Override
	public PaginationData<DeliveryRuleResponse> getActiveRules(int page, int size) {
		return repository.getActive(page, size);
	}

	@Override
	public Long createRule(DeliveryRuleCreateRequest request) {

		return repository.create(request);
	}

	@Override
	public void updateRule(Long id, DeliveryRuleUpdateRequest request) {
		repository.update(id, request);

	}

	@Override
	public void deleteRule(Long id) {
		repository.delete(id);
	}
}
