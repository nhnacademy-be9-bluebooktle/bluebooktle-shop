package shop.bluebooktle.common.dto.coupon.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;
import shop.bluebooktle.common.domain.coupon.CouponIssueType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailedCouponIssueResponse {

	private Long id;
	private String issueId;

	private Long userId;
	private Long couponId;

	private CouponIssueType issueType;
	private CouponIssueStatus status;

	private int retryCount;
	private String reason;

	private LocalDateTime availableStartAt;
	private LocalDateTime availableEndAt;
	private LocalDateTime createdAt;
	private LocalDateTime lastUpdatedAt;
}
