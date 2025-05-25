package shop.bluebooktle.backend.coupon.batch.birthday;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;

@Component
@RequiredArgsConstructor
@Slf4j
public class BirthdayCouponProcessor implements ItemProcessor<User, UserCoupon> {

	private final CouponRepository couponRepository;
	private final UserCouponRepository userCouponRepository;

	@Override
	public UserCoupon process(User user) {
		Coupon coupon = couponRepository.findByCouponName("생일 축하 쿠폰") //TODO [쿠폰] 로직 수정
			.orElseThrow(CouponNotFoundException::new);

		LocalDateTime startAt = LocalDate.now().withDayOfMonth(1).atStartOfDay();
		LocalDateTime endAt = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);

		boolean alreadyIssued = userCouponRepository.existsByUserAndCouponAndAvailableStartAtBetween(user, coupon,
			startAt, endAt);
		if (alreadyIssued) {
			log.info("{} 님의 생일 쿠폰이 이미 발급되었습니다.", user.getName());
			return null;
		}

		return UserCoupon.builder()
			.user(user)
			.coupon(coupon)
			.availableStartAt(startAt)
			.availableEndAt(endAt)
			.build();
	}
}