package shop.bluebooktle.common.dto.coupon.response;

import java.math.BigDecimal;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UsableUserCouponResponse {
	Long userCouponId;
	Long couponId;
	String couponName;
	String couponTypeName;
	BigDecimal minimumPayment;
	BigDecimal discountPrice;
	BigDecimal maximumDiscountPrice;
	Integer discountPercent;

	String bookName;
	String categoryName;

	@QueryProjection
	public UsableUserCouponResponse(Long userCouponId, Long couponId, String couponName, String couponTypeName,
		BigDecimal minimumPayment, BigDecimal discountPrice, BigDecimal maximumDiscountPrice, Integer discountPercent,
		String bookName, String categoryName) {
		this.userCouponId = userCouponId;
		this.couponId = couponId;
		this.couponName = couponName;
		this.couponTypeName = couponTypeName;
		this.minimumPayment = minimumPayment;
		this.discountPrice = discountPrice;
		this.maximumDiscountPrice = maximumDiscountPrice;
		this.discountPercent = discountPercent;
		this.bookName = bookName;
		this.categoryName = categoryName;
	}
}