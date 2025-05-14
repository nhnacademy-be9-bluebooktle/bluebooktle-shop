package shop.bluebooktle.common.dto.coupon.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Value;
import shop.bluebooktle.common.domain.CouponTypeTarget;

@Value
public class CouponResponse {
	Long id;
	String couponName;
	String couponTypeName;
	CouponTypeTarget target;
	LocalDateTime createdAt;

	@QueryProjection
	public CouponResponse(Long id, String couponName, String couponTypeName, CouponTypeTarget target,
		LocalDateTime createdAt) {
		this.id = id;
		this.couponName = couponName;
		this.couponTypeName = couponTypeName;
		this.target = target;
		this.createdAt = createdAt;
	}
}
