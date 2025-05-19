package shop.bluebooktle.backend.payment.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.payment.entity.Payment;

public record PaymentRequest(
	@NotNull
	@Positive
	Long orderId,

	@NotNull
	@PositiveOrZero(message = "결제 금액은 0원 이상이어야 합니다.")
	BigDecimal originalAmount,

	@PositiveOrZero(message = "포인트 사용 금액은 0원 이상이어야 합니다.")
	BigDecimal pointAmount,

	@NotNull
	@PositiveOrZero(message = "최종 결제 금액은 0원 이상이어야 합니다.")
	BigDecimal finalAmount) {

	public Payment toEntity(Order order) {
		return Payment.builder()
			.order(order)
			.pointAmount(pointAmount)
			.originalAmount(originalAmount)
			.finalAmount(finalAmount)
			.build();
	}
}
