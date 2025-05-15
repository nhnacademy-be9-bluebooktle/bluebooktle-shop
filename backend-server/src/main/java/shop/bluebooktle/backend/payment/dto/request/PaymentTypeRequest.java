package shop.bluebooktle.backend.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import shop.bluebooktle.backend.payment.entity.PaymentType;

public record PaymentTypeRequest(@NotBlank String method) {

	public PaymentType toEntity() {
		return PaymentType.builder()
			.method(this.method)
			.build();
	}
}
