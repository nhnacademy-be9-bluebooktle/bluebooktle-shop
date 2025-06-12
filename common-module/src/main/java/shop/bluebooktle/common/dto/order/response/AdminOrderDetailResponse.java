package shop.bluebooktle.common.dto.order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.refund.RefundReason;

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
	BigDecimal productAmount,
	BigDecimal totalPackagingFee,
	BigDecimal pointUserAmount,
	BigDecimal couponDiscountAmount,
	BigDecimal deliveryFee,
	BigDecimal paidAmount,
	OrderStatus orderStatus,
	String trackingNumber,
	RefundReason refundReason,
	String refundReasonDetail
) {
}
