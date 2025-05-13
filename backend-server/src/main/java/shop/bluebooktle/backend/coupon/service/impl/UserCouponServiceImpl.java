package shop.bluebooktle.backend.coupon.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.coupon.service.UserCouponService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;
import shop.bluebooktle.common.exception.coupon.UserCouponNotFoundException;

@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {

	private final UserCouponRepository userCouponRepository;
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;

	// 쿠폰 발급
	@Override
	@Transactional
	public void registerCoupon(User user, UserCouponRegisterRequest request) {
		User currentUser = userRepository.findById(user.getId())
			.orElseThrow(UserNotFoundException::new);
		Coupon coupon = couponRepository.findById(request.getCouponId())
			.orElseThrow(CouponNotFoundException::new);

		UserCoupon userCoupon = UserCoupon.builder()
			.coupon(coupon)
			.user(currentUser)
			.availableStartAt(request.getAvailableStartAt())
			.availableEndAt(request.getAvailableEndAt())
			.build();
		userCouponRepository.save(userCoupon);
	}

	// 유저 별 쿠폰 전체 조회
	@Override
	@Transactional(readOnly = true)
	public Page<UserCouponResponse> getAllUserCoupons(User user, Pageable pageable) {
		return userCouponRepository.findAllByUserCoupon(user, pageable);
	}

	// 유저 별 사용 가능 쿠폰 조회 - 결제용
	@Override
	@Transactional(readOnly = true)
	public Page<UserCouponResponse> getAvailableUserCoupons(User user, Pageable pageable) {
		return userCouponRepository.findAllByAvailableUserCoupon(user, pageable);
	}

	// 쿠폰 사용
	@Override
	public void useCoupon(Long id) {
		UserCoupon userCoupon = userCouponRepository.findById(id)
			.orElseThrow(UserCouponNotFoundException::new);

		userCoupon.useCoupon();
	}

	// 유저 쿠폰 삭제
	@Override
	@Transactional
	public void deleteCoupon(Long id) {
		UserCoupon userCoupon = userCouponRepository.findById(id)
			.orElseThrow(UserCouponNotFoundException::new);
		userCouponRepository.delete(userCoupon);
	}
}
