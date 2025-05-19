package shop.bluebooktle.backend.payment.dto.response;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import shop.bluebooktle.backend.payment.entity.Payment;

public record PaymentResponse(
	@NotNull
	@Min(0)
	Long id,

	@NotNull
	Long orderId,

	@NotNull
	@PositiveOrZero(message = "결제 금액은 0원 이상이어야 합니다.")
	BigDecimal originalAmount,

	@PositiveOrZero(message = "포인트 사용 금액은 0원 이상이어야 합니다.")
	BigDecimal pointAmount,

	@NotNull
	@PositiveOrZero(message = "최종 결제 금액은 0원 이상이어야 합니다.")
	BigDecimal finalAmount

) {
	public static PaymentResponse fromEntity(Payment payment) {
		return new PaymentResponse(
			payment.getId(),
			payment.getOrder().getId(),
			payment.getOriginalAmount(),
			payment.getPointAmount(),
			payment.getFinalAmount()
		);
	}
}
