package shop.bluebooktle.backend.coupon.repository;

import java.util.List;

import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;

public interface UserCouponQueryRepository {
	UsableUserCouponMapResponse findAllByUsableUserCouponForOrder(Long userId, List<Long> bookIds);

	Long couponAllUsableCoupons(Long userId);

	Long couponExpiringThisMonth(Long userId);
}
