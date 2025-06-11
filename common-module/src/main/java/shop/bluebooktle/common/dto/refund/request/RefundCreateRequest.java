package shop.bluebooktle.common.dto.refund.request;

import jakarta.validation.constraints.NotNull;
import shop.bluebooktle.common.domain.refund.RefundReason;

public record RefundCreateRequest(

	@NotNull(message = "주문 ID는 필수입니다.")
	String orderKey,

	@NotNull(message = "환불 사유는 필수입니다.")
	RefundReason reason,

	@NotNull(message = "환불 사유 상세는 필수입니다.")
	String reasonDetail
) {
}
