package shop.bluebooktle.common.dto.order.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DeliveryRuleUpdateRequest(

	@NotNull(message = "배송비를 입력해주세요.")
	@DecimalMin(value = "0.0", inclusive = true, message = "배송비는 0 이상이어야 합니다.")
	BigDecimal deliveryFee,

	@DecimalMin(value = "0.0", inclusive = true, message = "무료배송 기준 금액은 0 이상이어야 합니다.")
	BigDecimal freeDeliveryThreshold,

	@NotNull(message = "활성화 여부를 선택해주세요.")
	Boolean isActive
) {
}
