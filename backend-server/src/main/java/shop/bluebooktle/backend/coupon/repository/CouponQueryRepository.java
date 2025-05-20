package shop.bluebooktle.backend.coupon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.coupon.dto.CouponSearchRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.entity.auth.User;

public interface CouponQueryRepository {
	// 쿠폰 전체 조회
	Page<CouponResponse> findAllByCoupon(CouponSearchRequest request, Pageable pageable);

	// 쿠폰 정책 전체 조회
	Page<CouponTypeResponse> findAllByCouponType(Pageable pageable);

	// 유저 별 쿠폰 전체 조회
	Page<UserCouponResponse> findAllByUserCoupon(User user, Pageable pageable);

	// 유저 별 사용 가능 쿠폰 전체 조회
	Page<UserCouponResponse> findAllByUsableUserCoupon(User user, Pageable pageable);

	// 유저 별 사용 완료 쿠폰 조회
	Page<UserCouponResponse> findAllByUsedUserCoupon(User user, Pageable pageable);

	// 유저 별 기간 만료 쿠폰 조회
	Page<UserCouponResponse> findAllByExpiredUserCoupon(User user, Pageable pageable);
}
