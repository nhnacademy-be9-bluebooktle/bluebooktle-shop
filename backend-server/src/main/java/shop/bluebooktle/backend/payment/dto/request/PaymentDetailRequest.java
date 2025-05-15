package shop.bluebooktle.backend.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;

public record PaymentDetailRequest(
	@NotNull Long paymentTypeId,
	@NotBlank String key
) {
	public PaymentDetail toEntity(PaymentType paymentType) {
		return PaymentDetail.builder()
			.paymentType(paymentType)
			.key(key)
			.build();
	}
}
