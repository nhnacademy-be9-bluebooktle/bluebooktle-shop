package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;

public interface AdminCouponService {
	void registerCouponType(CouponTypeRegisterRequest request);

	PaginationData<CouponTypeResponse> getAllCouponTypes();

	void registerCoupon(CouponRegisterRequest request);

	PaginationData<CouponResponse> getAllCoupons();

	void issueCoupon(UserCouponRegisterRequest request);
}
