package shop.bluebooktle.common.dto.coupon.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Value;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;

@Value
public class UserCouponResponse {
	Long id;
	LocalDateTime createdAt;
	String couponName;
	String couponTypeName;
	CouponTypeTarget target;
	LocalDateTime availableStartAt;
	LocalDateTime availableEndAt;
	LocalDateTime usedAt;
	// [발급일] [쿠폰이름] [쿠폰정책이름] [적용 범위] [사용 가능 시작일] [사용 가능 종료일] [사용일]

	@QueryProjection
	public UserCouponResponse(Long id, LocalDateTime createdAt, String couponName, String couponTypeName,
		CouponTypeTarget target,
		LocalDateTime availableStartAt, LocalDateTime availableEndAt, LocalDateTime usedAt) {
		this.id = id;
		this.createdAt = createdAt;
		this.couponName = couponName;
		this.couponTypeName = couponTypeName;
		this.target = target;
		this.availableStartAt = availableStartAt;
		this.availableEndAt = availableEndAt;
		this.usedAt = usedAt;
	}
}
