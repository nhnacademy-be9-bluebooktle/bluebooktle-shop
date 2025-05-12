package shop.bluebooktle.common.dto.coupon.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import shop.bluebooktle.common.domain.CouponTypeTarget;

@Value
@Builder
@AllArgsConstructor
public class CouponResponse {
	Long id;
	String couponName;
	String couponTypeName;
	CouponTypeTarget target;
	LocalDateTime availableStartAt;
	LocalDateTime availableEndAt;
	LocalDateTime createdAt;
}
