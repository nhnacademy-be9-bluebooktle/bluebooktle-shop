package shop.bluebooktle.common.dto.coupon.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppliedCouponResponse {
	private Long userCouponBookOrderId;

	private Long userCouponId;
	private LocalDateTime userCouponCreatedAt;
	private LocalDateTime availableStartAt;
	private LocalDateTime availableEndAt;
	private LocalDateTime usedAt;

	private String couponName;

	private String couponTypeName;
	private CouponTypeTarget target;

	private String policyTypeDescription;
	private BigDecimal originalDiscountValue;
	private BigDecimal maxDiscountAmountForPercentage;
	private BigDecimal appliedDiscountAmount;
}