package shop.bluebooktle.backend.coupon.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
public class WelcomeCouponListener {

	private final UserCouponRepository userCouponRepository;
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;

	@RabbitListener(queues = "coupon.issue.welcome.queue")
	public void handleWelcomeCoupon(WelcomeCouponIssueMessage message) {
		User user = userRepository.findById(message.getCouponId())
			.orElseThrow(UserNotFoundException::new);

		Coupon coupon = couponRepository.findById(message.getCouponId())
			.orElseThrow(CouponNotFoundException::new);
		try {
			UserCoupon userCoupon = UserCoupon.builder()
				.user(user)
				.coupon(coupon)
				.availableStartAt(message.getAvailableStartAt())
				.availableEndAt(message.getAvailableEndAt())
				.build();

			userCouponRepository.save(userCoupon);
			log.info("웰컴 쿠폰 발급 완료: userId={}, couponId={}", message.getUserId(), message.getCouponId());
		} catch (Exception e) {
			log.error("웰컴 쿠폰 발급 실패: {}", message, e);
			// TODO 실패 시 처리
		}
	}
}

