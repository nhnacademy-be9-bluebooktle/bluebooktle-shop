package shop.bluebooktle.common.dto.coupon.response;

import java.math.BigDecimal;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Value;
import shop.bluebooktle.common.domain.CouponTypeTarget;

@Value
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

	@QueryProjection
	public CouponTypeResponse(Long id, String name, CouponTypeTarget target, BigDecimal minimumPayment,
		BigDecimal discountPrice, BigDecimal maximumDiscountPrice, Integer discountPercent) {
		this.id = id;
		this.name = name;
		this.target = target;
		this.minimumPayment = minimumPayment;
		this.discountPrice = discountPrice;
		this.maximumDiscountPrice = maximumDiscountPrice;
		this.discountPercent = discountPercent;
	}
}
