package shop.bluebooktle.common.dto.payment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TossConfirmRequest(

	@NotBlank
	@Size(min = 1, max = 200)
	String paymentKey,
	@NotBlank
	@Size(min = 6, max = 64)
	String orderId,

	Integer amount
) {
}