package shop.bluebooktle.backend.coupon.service;

import java.util.List;

import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;

public interface UserCouponService {
	// 주문에 해당하는 쿠폰 조회
	UsableUserCouponMapResponse getUsableCouponsForOrder(Long userId, List<Long> bookIds);

	// 쿠폰 사용 - used_at = now
	void useCoupon(Long id);

	void cancelCouponUse(Long id);
}
