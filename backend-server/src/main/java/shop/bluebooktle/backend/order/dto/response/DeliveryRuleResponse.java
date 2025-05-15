package shop.bluebooktle.backend.order.dto.response;

import java.math.BigDecimal;

import shop.bluebooktle.backend.order.entity.DeliveryRule;

public record DeliveryRuleResponse(
	Long id,
	String name,
	BigDecimal price,
	BigDecimal deliveryFee
) {
	public static DeliveryRuleResponse from(DeliveryRule rule) {
		return new DeliveryRuleResponse(
			rule.getId(),
			rule.getName(),
			rule.getPrice(),
			rule.getDeliveryFee()
		);
	}
}