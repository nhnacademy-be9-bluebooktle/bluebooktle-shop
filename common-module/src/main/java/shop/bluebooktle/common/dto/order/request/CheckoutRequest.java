package shop.bluebooktle.common.dto.order.request;

import java.time.LocalDate;
import java.util.List;

public record CheckoutRequest(
	List<OrderItemRequest> items,

	// 요청 배송일
	LocalDate requestedDeliveryDate,

	// 주문자
	String ordererName,
	String ordererPhoneNumber,

	// 수령자
	String receiverName,
	String receiverPhoneNumber,

	// 배송 주소
	String postalCode,
	String address,
	String detailAddress,

	// 전체 쿠폰/포인트
	Long orderCouponId,
	Integer pointsToUse
) {
}