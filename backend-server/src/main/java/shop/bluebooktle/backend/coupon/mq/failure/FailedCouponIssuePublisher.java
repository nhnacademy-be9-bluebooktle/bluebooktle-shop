package shop.bluebooktle.backend.coupon.mq.failure;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.entity.FailedCouponIssue;
import shop.bluebooktle.backend.coupon.mq.properties.ExchangeProperties;
import shop.bluebooktle.backend.coupon.mq.properties.QueueProperties;
import shop.bluebooktle.common.domain.coupon.CouponIssueType;

@Component
@RequiredArgsConstructor
public class FailedCouponIssuePublisher {
	private final RabbitTemplate rabbitTemplate;
	private final ExchangeProperties exchangeProperties;
	private final QueueProperties queueProperties;

	public void resend(FailedCouponIssue issue) {
		CouponIssueMessage message = CouponIssueMessage.builder()
			.userId(issue.getUserId())
			.couponId(issue.getCouponId())
			.availableStartAt(issue.getAvailableStartAt())
			.availableEndAt(issue.getAvailableEndAt())
			.build();

		String exchange = resolveExchange(issue.getIssueType());
		String routingKey = resolveRoutingKey(issue.getIssueType());

		rabbitTemplate.convertAndSend(exchange, routingKey, message);
	}

	private String resolveExchange(CouponIssueType type) {
		return switch (type) {
			case BIRTHDAY -> exchangeProperties.getBirthday();
			case WELCOME -> exchangeProperties.getWelcome();
			case DIRECT -> exchangeProperties.getDirect();
		};
	}

	private String resolveRoutingKey(CouponIssueType type) {
		return switch (type) {
			case BIRTHDAY -> queueProperties.getBirthday();
			case WELCOME -> queueProperties.getWelcome();
			case DIRECT -> queueProperties.getDirect();
		};
	}
}
