package shop.bluebooktle.common.dto.coupon.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;

@Getter
@Setter
@NoArgsConstructor
public class UserCouponResponse {
	Long id;
	LocalDateTime createdAt;
	String couponName;
	String couponTypeName;
	CouponTypeTarget target;
	LocalDateTime availableStartAt;
	LocalDateTime availableEndAt;
	LocalDateTime usedAt;

	String categoryName;
	String bookName;

	@QueryProjection
	public UserCouponResponse(Long id, LocalDateTime createdAt, String couponName, String couponTypeName,
		CouponTypeTarget target, LocalDateTime availableStartAt, LocalDateTime availableEndAt,
		LocalDateTime usedAt, String categoryName, String bookName) {
		this.id = id;
		this.createdAt = createdAt;
		this.couponName = couponName;
		this.couponTypeName = couponTypeName;
		this.target = target;
		this.availableStartAt = availableStartAt;
		this.availableEndAt = availableEndAt;
		this.usedAt = usedAt;
		this.categoryName = categoryName;
		this.bookName = bookName;
	}
}
