package shop.bluebooktle.backend.order.service.impl;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.repository.DeliveryRuleRepository;
import shop.bluebooktle.backend.order.service.DeliveryRuleService;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleUpdateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.exception.order.delivery_rule.CannotDeleteDefaultPolicyException;
import shop.bluebooktle.common.exception.order.delivery_rule.DefaultDeliveryRuleNotFoundException;
import shop.bluebooktle.common.exception.order.delivery_rule.DeliveryRuleAlreadyExistsException;
import shop.bluebooktle.common.exception.order.delivery_rule.DeliveryRuleNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryRuleServiceImpl implements DeliveryRuleService {

	private static final Region DEFAULT_REGION = Region.ALL;
	private final DeliveryRuleRepository repository;

	@Override
	@Transactional(readOnly = true)
	public DeliveryRuleResponse getDefaultRule() {
		return repository.findByRegion(DEFAULT_REGION)
			.map(this::toResponse)
			.orElseThrow(DefaultDeliveryRuleNotFoundException::new);
	}

	@Override
	@Transactional(readOnly = true)
	public DeliveryRuleResponse getRule(Long id) {
		return repository.findById(id)
			.map(this::toResponse)
			.orElseThrow(DeliveryRuleNotFoundException::new);
	}

	@Override
	public Long createRule(DeliveryRuleCreateRequest request) {
		if (repository.existsByRuleName(request.ruleName())) {
			throw new DeliveryRuleAlreadyExistsException(request.ruleName());
		}
		// 기본(ALL) 정책은 하나만 허용
		if (request.region() == DEFAULT_REGION
			&& repository.findByRegionAndIsActiveTrue(DEFAULT_REGION).isPresent()) {
			throw new DeliveryRuleAlreadyExistsException("기본 배송 정책은 이미 존재합니다.");
		}

		DeliveryRule rule = DeliveryRule.builder()
			.ruleName(request.ruleName())
			.deliveryFee(request.deliveryFee())
			.freeDeliveryThreshold(request.freeDeliveryThreshold())
			.region(request.region())
			.isActive(request.isActive())
			.build();
		return repository.save(rule).getId();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DeliveryRuleResponse> getAll(Pageable pageable) {
		return repository.findAll(pageable)
			.map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DeliveryRuleResponse> getAllByIsActive(Pageable pageable) {
		return repository.findAllByIsActiveTrue(pageable)
			.map(this::toResponse);
	}

	@Override
	public void deleteRule(Long id) {
		DeliveryRule rule = repository.findById(id)
			.orElseThrow(DeliveryRuleNotFoundException::new);
		if (rule.getRegion() == DEFAULT_REGION) {
			throw new CannotDeleteDefaultPolicyException();
		}
		repository.delete(rule);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal calculateDeliveryFee(BigDecimal orderAmount, Region region) {
		DeliveryRule defaultRule = repository.findByRegionAndIsActiveTrue(DEFAULT_REGION)
			.orElseThrow(DefaultDeliveryRuleNotFoundException::new);
		BigDecimal fee = orderAmount.compareTo(defaultRule.getFreeDeliveryThreshold()) >= 0
			? BigDecimal.ZERO
			: defaultRule.getDeliveryFee();
		return repository.findByRegionAndIsActiveTrue(region)
			.map(r -> fee.add(r.getDeliveryFee()))
			.orElse(fee);
	}

	@Override
	public void updateRule(Long id, DeliveryRuleUpdateRequest request) {
		DeliveryRule rule = repository.findById(id).orElseThrow(DeliveryRuleNotFoundException::new);
		rule.setDeliveryFee(request.deliveryFee());
		rule.setFreeDeliveryThreshold(request.freeDeliveryThreshold());
		rule.setIsActive(request.isActive());
		repository.save(rule);
	}

	private DeliveryRuleResponse toResponse(DeliveryRule entity) {
		return new DeliveryRuleResponse(
			entity.getId(),
			entity.getRuleName(),
			entity.getDeliveryFee(),
			entity.getFreeDeliveryThreshold(),
			entity.getRegion(),
			entity.getIsActive()
		);
	}
}
