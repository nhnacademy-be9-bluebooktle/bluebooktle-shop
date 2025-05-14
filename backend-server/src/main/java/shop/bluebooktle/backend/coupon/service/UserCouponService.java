package shop.bluebooktle.backend.coupon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.entity.auth.User;

public interface UserCouponService {
	// 쿠폰 발급
	void registerCoupon(User user, UserCouponRegisterRequest request);

	// 유저 별 쿠폰 전체 조회
	Page<UserCouponResponse> getAllUserCoupons(User user, Pageable pageable);

	// 유저 별 사용 가능 쿠폰 조회
	Page<UserCouponResponse> getAvailableUserCoupons(User user, Pageable pageable);

	// 쿠폰 사용 - used_at = now
	void useCoupon(Long id);

	// 쿠폰 삭제
	void deleteCoupon(Long id);
}
