package shop.bluebooktle.common.dto.order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import shop.bluebooktle.common.domain.order.OrderStatus;

public record AdminOrderListResponse(
	Long orderId,
	String orderKey,
	LocalDateTime orderDate,
	String ordererName,
	String ordererLoginId,
	String receiverName,
	BigDecimal totalAmount,
	OrderStatus orderStatus,
	String paymentMethod
) {
}