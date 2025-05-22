package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.frontend.repository.CouponRepository;
import shop.bluebooktle.frontend.service.CouponService;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {

	public final CouponRepository couponRepository;

	@Override
	public PaginationData<UserCouponResponse> getAllCoupons() {
		return couponRepository.getAllCoupon();
	}

	@Override
	public PaginationData<UserCouponResponse> getUsableCoupons() {
		return couponRepository.getAllCouponUsable();
	}

	@Override
	public PaginationData<UserCouponResponse> getUsedCoupons() {
		return couponRepository.getAllCouponUsed();
	}

	@Override
	public PaginationData<UserCouponResponse> getExpiredCoupons() {
		return couponRepository.getExpiredCoupon();
	}

	@Override
	public void deleteUserCoupon(Long userCouponId) {
		couponRepository.deleteUserCoupon(userCouponId);
	}

}
