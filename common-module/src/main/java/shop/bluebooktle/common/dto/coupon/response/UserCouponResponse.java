package shop.bluebooktle.common.dto.coupon.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import shop.bluebooktle.common.domain.CouponTypeTarget;

public record UserCouponResponse(
	Long id,
	LocalDateTime createdAt,
	String couponName,
	String couponTypeName,
	CouponTypeTarget target,
	LocalDateTime availableStartAt,
	LocalDateTime availableEndAt,
	LocalDateTime usedAt) {

	@QueryProjection
	public UserCouponResponse {
	}
}
