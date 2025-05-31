package shop.bluebooktle.auth.mq.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.auth.mq.properties.ExchangeProperties;
import shop.bluebooktle.auth.mq.properties.QueueProperties;
import shop.bluebooktle.common.dto.coupon.request.WelcomeCouponIssueMessage;

@Component
@RequiredArgsConstructor
public class WelcomeCouponIssueProducer {

	private final RabbitTemplate rabbitTemplate;
	private final QueueProperties queueProperties;
	private final ExchangeProperties exchangeProperties;

	public void send(WelcomeCouponIssueMessage message) {
		rabbitTemplate.convertAndSend(
			exchangeProperties.getWelcome(),
			queueProperties.getWelcome(),
			message
		);
	}
}