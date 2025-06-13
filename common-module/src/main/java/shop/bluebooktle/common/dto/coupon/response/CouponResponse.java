package shop.bluebooktle.common.dto.coupon.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;

@Getter
@NoArgsConstructor
@Builder
public class CouponResponse {
	Long id;
	String couponName;
	CouponTypeTarget target;
	String couponTypeName;
	BigDecimal minimumPayment;
	LocalDateTime createdAt;

	String categoryName;
	String bookName;

	@QueryProjection
	public CouponResponse(Long id, String couponName, CouponTypeTarget target, String couponTypeName,
		BigDecimal minimumPayment, LocalDateTime createdAt, String categoryName, String bookName) {
		this.id = id;
		this.couponName = couponName;
		this.target = target;
		this.couponTypeName = couponTypeName;
		this.minimumPayment = minimumPayment;
		this.createdAt = createdAt;
		this.categoryName = categoryName;
		this.bookName = bookName;
	}
}