package shop.bluebooktle.common.dto.coupon.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import shop.bluebooktle.common.domain.CouponTypeTarget;

@Value
@Builder
@AllArgsConstructor
public class CouponTypeResponse {
	Long id;
	String name;
	CouponTypeTarget target;
	BigDecimal minimumPayment;

	//절댓값
	BigDecimal discountPrice;

	//상댓값
	BigDecimal maximumDiscountPrice;
	Integer discountPercent;
}
