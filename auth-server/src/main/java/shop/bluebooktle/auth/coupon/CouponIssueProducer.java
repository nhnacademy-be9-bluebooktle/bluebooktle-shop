package shop.bluebooktle.auth.coupon;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.coupon.request.WelcomeCouponIssueMessage;

@Component
@RequiredArgsConstructor
public class CouponIssueProducer {

	private final RabbitTemplate rabbitTemplate;

	public void sendWelcomeCoupon(WelcomeCouponIssueMessage message) {
		rabbitTemplate.convertAndSend(
			"coupon.issue.welcome.exchange",
			"coupon.issue.welcome.routing-key",
			message
		);
	}
}