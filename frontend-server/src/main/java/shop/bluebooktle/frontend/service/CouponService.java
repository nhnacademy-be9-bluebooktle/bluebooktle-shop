package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;

public interface CouponService {
	PaginationData<UserCouponResponse> getAllCoupons();

	PaginationData<UserCouponResponse> getUsableCoupons();

	PaginationData<UserCouponResponse> getUsedCoupons();

	PaginationData<UserCouponResponse> getExpiredCoupons();

	void deleteUserCoupon(Long userCouponId);
}
