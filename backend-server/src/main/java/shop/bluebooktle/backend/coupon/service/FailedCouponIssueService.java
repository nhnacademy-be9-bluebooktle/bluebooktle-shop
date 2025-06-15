package shop.bluebooktle.backend.coupon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.coupon.request.FailedCouponIssueSearchRequest;
import shop.bluebooktle.common.dto.coupon.response.FailedCouponIssueResponse;

public interface FailedCouponIssueService {
	void resend(Long issueId);

	void resendAll();

	Page<FailedCouponIssueResponse> getAllFailedCoupons(FailedCouponIssueSearchRequest request, Pageable pageable);
}
