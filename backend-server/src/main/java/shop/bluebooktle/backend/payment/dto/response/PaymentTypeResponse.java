package shop.bluebooktle.backend.payment.dto.response;

import jakarta.validation.constraints.NotNull;
import shop.bluebooktle.backend.payment.entity.PaymentType;

public record PaymentTypeResponse(@NotNull Long id, @NotNull String method) {

	public static PaymentTypeResponse fromEntity(@NotNull PaymentType paymentType) {
		return new PaymentTypeResponse(paymentType.getId(), paymentType.getMethod());
	}
}