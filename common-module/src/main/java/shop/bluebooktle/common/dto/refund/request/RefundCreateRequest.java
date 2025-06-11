package shop.bluebooktle.common.dto.refund.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import shop.bluebooktle.common.domain.RefundReason;

public record RefundCreateRequest(

	@NotNull(message = "주문 ID는 필수입니다.")
	@Positive(message = "주문 ID는 양수여야 합니다.")
	Long orderId,

	@NotNull(message = "환불 날짜는 필수입니다.")
	LocalDateTime date,

	@NotNull(message = "환불 사유는 필수입니다.")
	RefundReason reason,

	@NotNull(message = "환불 사유 상세는 필수입니다.")
	String reasonDetail,

	@NotNull(message = "환불 금액은 필수입니다.")
	@DecimalMin(value = "0.0", inclusive = true, message = "환불 금액은 0원 이상이어야 합니다.")
	BigDecimal refundAmount
) {
}
