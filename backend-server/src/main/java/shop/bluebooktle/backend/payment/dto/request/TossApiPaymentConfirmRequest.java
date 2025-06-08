package shop.bluebooktle.backend.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TossApiPaymentConfirmRequest(
	@NotBlank @Size(min = 1, max = 200) String paymentKey,
	@NotBlank @Size(min = 6, max = 64) String orderId,
	@NotNull Long amount
) {
}