package shop.bluebooktle.backend.coupon.mq.welcome;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.coupon.request.WelcomeCouponIssueMessage;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;

@Slf4j
@Component
@RequiredArgsConstructor
@RefreshScope
public class WelcomeCouponListener {

	private final UserCouponRepository userCouponRepository;
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;

	@RabbitListener(queues = "#{queueProperties.welcome}")
	public void handleWelcomeCoupon(WelcomeCouponIssueMessage message) {
		User user = userRepository.findById(message.getUserId())
			.orElseThrow(UserNotFoundException::new);

		Coupon coupon = couponRepository.findById(message.getCouponId())
			.orElseThrow(CouponNotFoundException::new);

		UserCoupon userCoupon = UserCoupon.builder()
			.user(user)
			.coupon(coupon)
			.availableStartAt(message.getAvailableStartAt())
			.availableEndAt(message.getAvailableEndAt())
			.build();

		userCouponRepository.save(userCoupon);
		log.info("웰컴 쿠폰 발급 완료: userId = {}, couponId = {}", message.getUserId(), message.getCouponId());
	}
}

