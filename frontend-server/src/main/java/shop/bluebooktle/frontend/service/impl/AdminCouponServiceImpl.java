package shop.bluebooktle.frontend.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.FailedCouponIssueSearchRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.FailedCouponIssueResponse;
import shop.bluebooktle.frontend.repository.AdminCouponRepository;
import shop.bluebooktle.frontend.service.AdminCouponService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCouponServiceImpl implements AdminCouponService {
	private final AdminCouponRepository adminCouponRepository;

	public void registerCouponType(CouponTypeRegisterRequest request) {
		adminCouponRepository.registerCouponType(request);
	}

	public PaginationData<CouponTypeResponse> getAllCouponType(Pageable pageable) {
		return adminCouponRepository.getAllCouponType(pageable.getPageNumber(), pageable.getPageSize());
	}

	public void registerCoupon(CouponRegisterRequest request) {
		adminCouponRepository.registerCoupon(request);
	}

	public PaginationData<CouponResponse> getAllCoupon(Pageable pageable, String searchCouponName) {
		return adminCouponRepository.getAllCoupon(pageable.getPageNumber(), pageable.getPageSize(), searchCouponName);
	}

	public void issueCoupon(UserCouponRegisterRequest request) {
		adminCouponRepository.issueCoupon(request);
	}

	public PaginationData<FailedCouponIssueResponse> getAllFailedCouponIssue(
		Pageable pageable,
		FailedCouponIssueSearchRequest request
	) {
		return adminCouponRepository.getAllFailedCouponIssue(
			request,
			pageable.getPageNumber(),
			pageable.getPageSize()
		);
	}

	public void resendFailedCoupon(Long issueId) {
		adminCouponRepository.resendFailedCoupon(issueId);
	}

	public void resendAllFailedCoupons() {
		adminCouponRepository.resendAllFailedCoupons();
	}
}
