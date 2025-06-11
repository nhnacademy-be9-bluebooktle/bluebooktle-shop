package shop.bluebooktle.common.dto.refund.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import shop.bluebooktle.common.domain.refund.RefundStatus;

public record RefundUpdateRequest(

	@NotNull(message = "환불 ID는 필수입니다.") // orderId -> refundId
	@Positive(message = "환불 ID는 양수여야 합니다.")
	Long refundId,

	@NotNull(message = "반품 상태는 필수입니다.")
	RefundStatus status
) {
}