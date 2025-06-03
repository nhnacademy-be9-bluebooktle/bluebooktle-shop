package shop.bluebooktle.common.dto.order.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
	@NotNull(message = "책 ID는 필수입니다.")
	Long bookId,

	@NotNull(message = "책 수량은 필수입니다.")
	@Min(value = 1, message = "책 수량은 최소 1개 이상이어야 합니다.")
	Integer bookQuantity,

	@NotNull(message = "책의 가격은 필수입니다.")
	@DecimalMin(value = "0.0", inclusive = true, message = "가격은 0원 이상이어야 합니다.")
	BigDecimal salePrice,

	Long packagingOptionId,

	Integer packagingQuantity,

	String itemCouponCode
) {
}
