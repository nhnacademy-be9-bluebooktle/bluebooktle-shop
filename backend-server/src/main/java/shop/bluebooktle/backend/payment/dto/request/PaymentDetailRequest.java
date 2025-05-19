package shop.bluebooktle.backend.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.common.domain.payment.PaymentStatus;

public record PaymentDetailRequest(
	@NotNull Long paymentId,
	@NotNull Long paymentTypeId,
	@NotBlank String paymentKey,
	@NotNull PaymentStatus paymentStatus
) {
	public PaymentDetail toEntity(Payment payment, PaymentType paymentType) {
		return PaymentDetail.builder()
			.payment(payment)
			.paymentType(paymentType)
			.paymentKey(paymentKey)
			.paymentStatus(paymentStatus)
			.build();
	}
}
