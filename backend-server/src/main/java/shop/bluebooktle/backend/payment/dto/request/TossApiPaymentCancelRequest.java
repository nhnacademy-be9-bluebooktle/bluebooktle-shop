package shop.bluebooktle.backend.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TossApiPaymentCancelRequest(
	@NotBlank @Size(min = 1, max = 200) String cancelReason
) {
}