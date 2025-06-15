package shop.bluebooktle.common.dto.coupon.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;
import shop.bluebooktle.common.domain.coupon.CouponIssueType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailedCouponIssueSearchRequest {
	private CouponIssueType type;
	private CouponIssueStatus status;
}
