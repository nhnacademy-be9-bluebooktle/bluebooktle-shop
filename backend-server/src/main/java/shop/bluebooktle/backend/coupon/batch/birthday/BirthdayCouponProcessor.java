package shop.bluebooktle.backend.coupon.batch.birthday;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;

@Component
@RequiredArgsConstructor
public class BirthdayCouponProcessor implements ItemProcessor<User, UserCoupon> {

	private final CouponRepository couponRepository;

	@Override
	public UserCoupon process(User user) {
		Coupon coupon = couponRepository.findByCouponName("생일 쿠폰") // TODO [쿠폰] 생일 쿠폰 규칙을 정해야하나,,,
			.orElseThrow(CouponNotFoundException::new);

		LocalDateTime now = LocalDateTime.now();

		return UserCoupon.builder()
			.user(user)
			.coupon(coupon)
			.availableStartAt(now.withDayOfMonth(1).with(LocalDateTime.MIN))
			.availableEndAt(now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).with(LocalTime.MAX))
			.build();
	}
}
