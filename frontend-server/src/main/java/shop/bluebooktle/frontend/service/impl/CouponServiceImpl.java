package shop.bluebooktle.frontend.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.UserCouponFilterType;
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
	public PaginationData<UserCouponResponse> getAllCoupons(String filter, Pageable pageable) {
		UserCouponFilterType filterType = UserCouponFilterType.valueOf(filter.toUpperCase());
		return couponRepository.getAllCoupons(filterType, pageable);
	}
}
