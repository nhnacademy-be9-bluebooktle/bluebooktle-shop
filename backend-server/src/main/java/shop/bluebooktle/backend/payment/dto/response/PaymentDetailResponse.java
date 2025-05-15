package shop.bluebooktle.backend.payment.dto.response;

import jakarta.validation.constraints.NotNull;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;

public record PaymentDetailResponse(
	@NotNull Long id,
	@NotNull Long paymentTypeId,
	String key
) {
	public static PaymentDetailResponse fromEntity(PaymentDetail e) {
		return new PaymentDetailResponse(
			e.getId(),
			e.getPaymentType().getId(),
			e.getKey()
		);
	}
}
