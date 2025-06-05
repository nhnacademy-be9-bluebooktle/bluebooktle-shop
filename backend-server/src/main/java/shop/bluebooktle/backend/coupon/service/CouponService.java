package shop.bluebooktle.backend.coupon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;

public interface CouponService {
	// 등록
	void registerCoupon(CouponRegisterRequest request);

	// 전체 조회
	Page<CouponResponse> getAllCoupons(String searchCouponName, Pageable pageable);
}
