package shop.bluebooktle.backend.payment.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.common.domain.payment.PaymentStatus;

public record PaymentDetailResponse(
	@NotNull
	Long id,

	@NotNull
	Long paymentTypeId,

	@Size(min = 1, max = 200, message = "토스 결제키는 1자 이상 200자 이하여야 합니다.")
	String paymentKey,

	@NotNull
	PaymentStatus paymentStatus
) {
	public static PaymentDetailResponse fromEntity(PaymentDetail e) {
		return new PaymentDetailResponse(
			e.getId(),
			e.getPaymentType().getId(),
			e.getPaymentKey(),
			e.getPaymentStatus()
		);
	}
}
