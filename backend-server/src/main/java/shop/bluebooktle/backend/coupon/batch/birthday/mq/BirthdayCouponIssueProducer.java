package shop.bluebooktle.backend.coupon.batch.birthday.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.mq.ExchangeProperties;
import shop.bluebooktle.backend.coupon.mq.QueueProperties;

@Slf4j
@RequiredArgsConstructor
@Service
public class BirthdayCouponIssueProducer {

	private final RabbitTemplate rabbitTemplate;
	private final QueueProperties queueProperties;
	private final ExchangeProperties exchangeProperties;

	public void send(CouponIssueMessage message) {
		rabbitTemplate.convertAndSend(
			exchangeProperties.getBirthday(),
			queueProperties.getBirthday(),
			message
		);
		log.info("birthday coupon issue : {}", message);
	}
}
