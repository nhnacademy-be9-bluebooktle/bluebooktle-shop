package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.frontend.repository.CouponRepository;
import shop.bluebooktle.frontend.service.CouponService;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {

	public final CouponRepository couponRepository;

	@Override
	public PaginationData<UserCouponResponse> getAllCoupons(String filter, Pageable pageable) {
		UserCouponFilterType filterType = UserCouponFilterType.valueOf(filter.toUpperCase());
		return couponRepository.getAllCoupons(filterType, pageable);
	}

	@Override
	public UsableUserCouponMapResponse getUsableCouponsForOrder(List<Long> bookIds) {
		return couponRepository.getUsableCouponsForOrder(bookIds);
	}

	@Override
	public Long countAllUsableCoupons() {
		return couponRepository.countAllUsableCoupons();
	}

	@Override
	public Long countExpiringThisMonth() {
		return couponRepository.countExpiringThisMonth();
	}
}