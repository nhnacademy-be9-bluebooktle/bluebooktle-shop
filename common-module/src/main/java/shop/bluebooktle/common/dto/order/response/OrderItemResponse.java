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
public class OrderItemResponse {
	private Long bookOrderId;
	private Long bookId;
	private String bookTitle;
	private String bookThumbnailUrl;
	private int quantity;
	private BigDecimal price;
	private List<OrderPackagingResponse> packagingOptions;
	private List<AppliedCouponResponse> appliedItemCoupons;
	private BigDecimal finalItemPrice;
}