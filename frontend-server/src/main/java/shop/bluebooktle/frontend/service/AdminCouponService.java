package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.FailedCouponIssueSearchRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.FailedCouponIssueResponse;

public interface AdminCouponService {
	void registerCouponType(CouponTypeRegisterRequest request);

	PaginationData<CouponTypeResponse> getAllCouponType(Pageable pageable);

	void registerCoupon(CouponRegisterRequest request);

	PaginationData<CouponResponse> getAllCoupon(Pageable pageable, String searchCouponName);

	void issueCoupon(UserCouponRegisterRequest request);

	PaginationData<FailedCouponIssueResponse> getAllFailedCouponIssue(Pageable pageable,
		FailedCouponIssueSearchRequest request);

	void resendFailedCoupon(Long issueId);

	void resendAllFailedCoupons();
}
