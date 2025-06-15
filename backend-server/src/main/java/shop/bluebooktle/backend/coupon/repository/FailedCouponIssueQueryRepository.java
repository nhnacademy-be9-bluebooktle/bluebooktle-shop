package shop.bluebooktle.backend.coupon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.coupon.request.FailedCouponIssueSearchRequest;
import shop.bluebooktle.common.dto.coupon.response.FailedCouponIssueResponse;

public interface FailedCouponIssueQueryRepository {
	Page<FailedCouponIssueResponse> findAllFailedCouponIssue(FailedCouponIssueSearchRequest request, Pageable pageable);
}
