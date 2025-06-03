package shop.bluebooktle.backend.coupon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.coupon.dto.CouponSearchRequest;
import shop.bluebooktle.common.domain.UserCouponFilterType;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;

public interface CouponQueryRepository {
	// 쿠폰 전체 조회
	Page<CouponResponse> findAllByCoupon(CouponSearchRequest request, Pageable pageable);

	// 쿠폰 정책 전체 조회
	Page<CouponTypeResponse> findAllByCouponType(Pageable pageable);

	// 유저 별 쿠폰 조회
	Page<UserCouponResponse> findAllByUserCoupon(Long userId, UserCouponFilterType filterType, Pageable pageable);

}
