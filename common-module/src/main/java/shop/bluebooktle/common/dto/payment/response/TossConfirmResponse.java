package shop.bluebooktle.common.dto.payment.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TossConfirmResponse(
	@NotBlank
	String status,
	@NotBlank
	@Size(min = 6, max = 64)
	String orderId,
	Integer totalAmount
) {
}