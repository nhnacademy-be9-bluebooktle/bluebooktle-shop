package shop.bluebooktle.backend.coupon.mq.direct;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Component
public class DirectCouponIssueListener {
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;
	private final UserCouponRepository userCouponRepository;

	@RabbitListener(queues = "#{queueProperties.direct}")
	public void handleCouponIssue(CouponIssueMessage message) {
		log.info("쿠폰 발급 message: {}", message);

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
		log.info("쿠폰 발급 완료 - user: {}", user.getLoginId());
	}
}
