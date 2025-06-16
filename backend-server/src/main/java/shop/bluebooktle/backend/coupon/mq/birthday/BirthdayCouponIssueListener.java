package shop.bluebooktle.backend.coupon.mq.birthday;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.client.CouponFailureMessageClient;
import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.user.dto.DoorayMessagePayload;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Component
@RefreshScope
public class BirthdayCouponIssueListener {
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;
	private final UserCouponRepository userCouponRepository;
	private final CouponFailureMessageClient messageClient;

	@RabbitListener(queues = "#{queueProperties.birthday}")
	public void birthdayCouponIssue(CouponIssueMessage message) {
		/*
		실패 테스트용 코드
		- 메서드 안에 있는 코드를 전부 주석처리 후 아래 코드를 사용하면 dlq 큐에 저장되는 것을 확인 가능
		log.info("실패 TEST : 생일 쿠폰 message: {}", message);
		throw new RuntimeException("실패 DLQ Test");
		 */
		try {
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

		} catch (Exception e) {
			log.error("생일 쿠폰 발급 실패: userId={}, couponId={}, error={}", message.getUserId(), message.getCouponId(),
				e.getMessage(), e);

			DoorayMessagePayload payload = new DoorayMessagePayload();
			payload.setBotName("생일 쿠폰 발급 실패");
			payload.setText(
				"실패 시간 = " + LocalDateTime.now() + "\n" +
					"유저 ID = " + message.getUserId() + "\n" +
					"쿠폰 ID = " + message.getCouponId()
			);
			messageClient.sendMessage(payload);

			throw e;
		}
	}
}
