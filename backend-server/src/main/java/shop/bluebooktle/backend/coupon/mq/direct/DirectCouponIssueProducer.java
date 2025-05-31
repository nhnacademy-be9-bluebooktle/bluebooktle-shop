package shop.bluebooktle.backend.coupon.mq.direct;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.mq.properties.ExchangeProperties;
import shop.bluebooktle.backend.coupon.mq.properties.QueueProperties;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectCouponIssueProducer {

	private final RabbitTemplate rabbitTemplate;
	private final QueueProperties queueProperties;
	private final ExchangeProperties exchangeProperties;

	public void send(CouponIssueMessage message) {
		rabbitTemplate.convertAndSend(
			exchangeProperties.getDirect(),
			queueProperties.getDirect(),
			message
		);
		log.info("MQ send Message: {}", message);
	}
}
