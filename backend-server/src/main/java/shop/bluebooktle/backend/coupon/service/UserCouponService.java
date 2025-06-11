package shop.bluebooktle.backend.coupon.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;

public interface UserCouponService {
	// 유저 별 쿠폰 전체 조회
	Page<UserCouponResponse> getAllUserCoupons(Long userId, UserCouponFilterType filterType, Pageable pageable);

	// 주문에 해당하는 쿠폰 조회
	UsableUserCouponMapResponse getUsableCouponsForOrder(Long userId, List<Long> bookIds);

	Long countAllUsableCoupons(Long userId);

	Long countExpiringThisMonth(Long userId);
}
