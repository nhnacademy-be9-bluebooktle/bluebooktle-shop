package shop.bluebooktle.backend.coupon.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.coupon.service.UserCouponService;
import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.exception.coupon.UserCouponNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCouponServiceImpl implements UserCouponService {

	private final UserCouponRepository userCouponRepository;

	@Override
	public Page<UserCouponResponse> getAllUserCoupons(Long userId, UserCouponFilterType filterType, Pageable pageable) {
		return userCouponRepository.findAllByUserCoupon(userId, filterType, pageable);
	}

	@Override
	public UsableUserCouponMapResponse getUsableCouponsForOrder(Long userId, List<Long> bookIds) {
		return userCouponRepository.findAllByUsableUserCouponForOrder(userId, bookIds);
	}

	@Override
	@Transactional
	public void useCoupon(Long id) {
		UserCoupon userCoupon = userCouponRepository.findById(id)
			.orElseThrow(UserCouponNotFoundException::new);
		userCoupon.useCoupon();
	}

	@Override
	@Transactional
	public void cancelCouponUse(Long id) {
		UserCoupon userCoupon = userCouponRepository.findById(id)
			.orElseThrow(UserCouponNotFoundException::new);
		userCoupon.cancelCoupon();
	}
}
