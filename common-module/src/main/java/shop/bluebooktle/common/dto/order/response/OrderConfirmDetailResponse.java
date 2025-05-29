package shop.bluebooktle.common.dto.order.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.dto.coupon.response.AppliedCouponResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderConfirmDetailResponse {
	private Long orderId;
	private String orderName;
	private String receiverName;
	private String receiverPhoneNumber;
	private String address;
	private String detailAddress;
	private String postalCode;
	private BigDecimal deliveryFee;
	private List<OrderItemResponse> orderItems;
	private List<AppliedCouponResponse> appliedCoupons;
	private BigDecimal userPointBalance;
	private BigDecimal subTotal;
	private BigDecimal packagingTotal;
	private BigDecimal couponDiscountTotal;
	private BigDecimal totalAmount;
}