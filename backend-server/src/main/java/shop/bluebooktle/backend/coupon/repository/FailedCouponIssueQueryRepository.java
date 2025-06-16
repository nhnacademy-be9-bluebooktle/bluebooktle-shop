package shop.bluebooktle.backend.coupon.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;
import shop.bluebooktle.common.domain.coupon.CouponIssueType;
import shop.bluebooktle.common.dto.coupon.request.FailedCouponIssueSearchRequest;
import shop.bluebooktle.common.dto.coupon.response.FailedCouponIssueResponse;

public interface FailedCouponIssueQueryRepository {
	Page<FailedCouponIssueResponse> findAllFailedCouponIssue(FailedCouponIssueSearchRequest request, Pageable pageable);

	Map<CouponIssueType, Long> countTodayTotalByType();

	Map<CouponIssueType, Long> countTodayByTypeAndStatus(CouponIssueStatus status);
}
