package shop.bluebooktle.backend.order.dto.request;

import java.math.BigDecimal;

public record DeliveryRuleRequest(
	String name,
	BigDecimal price,
	BigDecimal deliveryFee
) {
}
