package shop.bluebooktle.common.dto.refund.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.domain.refund.RefundStatus;

public record AdminRefundDetailResponse(
	Long refundId,
	LocalDateTime requestDate,
	RefundStatus status,
	BigDecimal refundAmount,
	RefundReason reason,
	String reasonDetail,
	Long orderId,
	String orderKey,
	String ordererName,
	String userLoginId
) {
}