package shop.bluebooktle.frontend.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;

public interface CouponService {
	PaginationData<UserCouponResponse> getAllCoupons(String filter, Pageable pageable);

	UsableUserCouponMapResponse getUsableCouponsForOrder(List<Long> bookIds);

	Long countAllUsableCoupons();

	Long countExpiringThisMonth();
}
