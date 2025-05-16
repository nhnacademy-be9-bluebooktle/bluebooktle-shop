package shop.bluebooktle.backend.coupon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;

public interface CouponTypeService {
	//쿠폰 정책 등록
	void registerCouponType(CouponTypeRegisterRequest couponTypeRegisterRequest);

	// 전체 쿠폰 정책 조회
	Page<CouponTypeResponse> getAllCouponTypeList(Pageable pageable);
}
