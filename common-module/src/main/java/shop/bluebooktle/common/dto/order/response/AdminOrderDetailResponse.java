package shop.bluebooktle.common.dto.order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import shop.bluebooktle.common.domain.order.OrderStatus;

public record AdminOrderDetailResponse(
	Long orderId,
	String orderKey,
	LocalDateTime orderDate,
	String ordererName,
	String ordererLoginId,
	String ordererPhoneNumber,
	String ordererEmail,
	String receiverName,
	String receiverPhoneNumber,
	String receiverEmail,
	String postalCode,
	String address,
	String detailAddress,
	List<OrderItemResponse> orderItems,
	String paymentMethod,
	BigDecimal originalAmount,
	BigDecimal pointUserAmount,
	BigDecimal couponDiscountAmount,
	BigDecimal deliveryFee,
	BigDecimal paidAmount,
	OrderStatus orderStatus,
	String trackingNumber
) {
}
