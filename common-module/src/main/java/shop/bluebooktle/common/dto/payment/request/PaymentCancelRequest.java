package shop.bluebooktle.common.dto.payment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaymentCancelRequest(
	@NotBlank
	@Size(min = 1, max = 255)
	String orderKey,

	String cancelReason
) {
}