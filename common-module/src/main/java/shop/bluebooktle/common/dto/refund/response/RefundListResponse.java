package shop.bluebooktle.common.dto.refund.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.domain.refund.RefundStatus;

public record RefundListResponse(
	Long refundId,
	Long orderId,
	String orderKey,
	String ordererName,
	LocalDateTime requestDate,
	BigDecimal refundPrice,
	RefundReason reason,
	RefundStatus status
) {
}