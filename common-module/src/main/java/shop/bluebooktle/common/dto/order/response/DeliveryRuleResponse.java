package shop.bluebooktle.common.dto.order.response;

import java.math.BigDecimal;

import shop.bluebooktle.common.domain.order.Region;

public record DeliveryRuleResponse(
	Long id,
	
	String ruleName,

	BigDecimal deliveryFee,

	BigDecimal freeDeliveryThreshold,

	Region region,

	Boolean isActive) {
}