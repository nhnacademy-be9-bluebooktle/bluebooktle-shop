package shop.bluebooktle.common.dto.order.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import shop.bluebooktle.common.domain.order.Region;

public record DeliveryRuleCreateRequest(

	@NotBlank(message = "룰 이름은 필수 값입니다.")
	@Size(max = 50, message = "룰 이름은 최대 50자까지 가능합니다.")
	String ruleName,

	@NotNull(message = "배송비는 필수 값입니다.")
	@DecimalMin(value = "0.0", inclusive = true, message = "배송비는 0 이상이어야 합니다.")
	BigDecimal deliveryFee,

	@DecimalMin(value = "0.0", inclusive = true, message = "무료 배송 기준 금액은 0 이상이어야 합니다.")
	BigDecimal freeDeliveryThreshold,

	@NotNull(message = "지역 정보는 필수 값입니다.")
	Region region,

	@NotNull(message = "활성 여부는 필수 값입니다.")
	Boolean isActive
) {
}
