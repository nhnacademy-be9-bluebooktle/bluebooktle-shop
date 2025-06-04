package shop.bluebooktle.common.dto.order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import shop.bluebooktle.common.domain.order.OrderStatus;

public record OrderHistoryResponse(
	Long orderId,
	LocalDateTime createAt,
	String orderName,
	BigDecimal totalPrice,
	String orderKey,
	OrderStatus orderStatus,
	String thumbnailUrl
) {
}
