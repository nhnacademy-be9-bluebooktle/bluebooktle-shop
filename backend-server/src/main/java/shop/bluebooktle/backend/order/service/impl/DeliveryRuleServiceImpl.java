package shop.bluebooktle.backend.order.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.repository.DeliveryRuleRepository;
import shop.bluebooktle.backend.order.service.DeliveryRuleService;
import shop.bluebooktle.common.exception.order.CannotDeleteDefaultPolicyException;
import shop.bluebooktle.common.exception.order.DeliveryRuleNotFoundException;

@Service
@RequiredArgsConstructor
public class DeliveryRuleServiceImpl implements DeliveryRuleService {

	private final DeliveryRuleRepository deliveryRuleRepository;

	private static final String DEFAULT_RULE_NAME = "기본 배송 정책";

	@Override
	@Transactional(readOnly = true)
	public DeliveryRule getDefaultRule() {
		return deliveryRuleRepository.findByName(DEFAULT_RULE_NAME)
			.orElseThrow(() -> new IllegalStateException("기본 배송 정책이 존재하지 않습니다."));
	}

	@Override
	@Transactional
	public DeliveryRule createPolicy(String name, BigDecimal price, BigDecimal deliveryFee) {
		if (deliveryRuleRepository.existsByName(name)) {
			throw new IllegalArgumentException("이미 존재하는 배송 정책명입니다: " + name);
		}

		DeliveryRule rule = DeliveryRule.builder()
			.name(name)
			.price(price)
			.deliveryFee(deliveryFee)
			.build();

		return deliveryRuleRepository.save(rule);
	}

	@Override
	@Transactional(readOnly = true)
	public DeliveryRule getRule(String name) {
		return deliveryRuleRepository.findByName(name)
			.orElseThrow(DeliveryRuleNotFoundException::new);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DeliveryRule> getAll() {
		return deliveryRuleRepository.findAll();
	}

	@Override
	@Transactional
	public void deletePolicy(Long id) {
		DeliveryRule rule = deliveryRuleRepository.findById(id)
			.orElseThrow(DeliveryRuleNotFoundException::new);

		// 기본 배송 정책은 삭제 불가
		if ("기본 배송 정책".equals(rule.getName())) {
			throw new CannotDeleteDefaultPolicyException();
		}

		deliveryRuleRepository.delete(rule); // soft delete
	}

}
