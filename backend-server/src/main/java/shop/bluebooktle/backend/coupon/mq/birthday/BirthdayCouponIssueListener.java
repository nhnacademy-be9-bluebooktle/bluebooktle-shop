package shop.bluebooktle.backend.coupon.mq.birthday;

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
public class BirthdayCouponIssueListener {
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;
	private final UserCouponRepository userCouponRepository;

	@RabbitListener(queues = "#{queueProperties.birthday}")
	public void birthdayCouponIssue(CouponIssueMessage message) {
		/*
		실패 테스트용 코드
		- 메서드 안에 있는 코드를 전부 주석처리 후 아래 코드를 사용하면 dlq 큐에 저장되는 것을 확인 가능
		log.info("실패 TEST : 생일 쿠폰 message: {}", message);
		throw new RuntimeException("실패 DLQ Test");
		 */
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
	}
}
