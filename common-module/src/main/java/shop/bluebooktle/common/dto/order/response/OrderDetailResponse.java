package shop.bluebooktle.common.dto.order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import shop.bluebooktle.common.domain.order.OrderStatus;

public record OrderDetailResponse(
	Long orderId,
	String orderKey,
	LocalDateTime orderDate,
	String ordererName,
	BigDecimal paidAmount,
	String paidMethod,
	OrderStatus orderStatus,
	String receiverName,
	String receiverPhoneNumber,
	String receiverEmail,
	String address,
	String detailAddress,
	String postalCode,
	List<OrderItemResponse> orderItemResponses,
	BigDecimal originalAmount,
	BigDecimal pointUserAmount,
	BigDecimal couponDiscountAmount,
	BigDecimal deliveryFee,
	String trackingNumber
) {
}
