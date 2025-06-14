package shop.bluebooktle.mq.welcome;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import shop.bluebooktle.auth.mq.producer.WelcomeCouponIssueProducer;
import shop.bluebooktle.auth.mq.properties.ExchangeProperties;
import shop.bluebooktle.auth.mq.properties.QueueProperties;
import shop.bluebooktle.common.dto.coupon.request.WelcomeCouponIssueMessage;

@ExtendWith(MockitoExtension.class)
class WelcomeCouponIssueProducerTest {

	@Mock
	private RabbitTemplate rabbitTemplate;

	@Mock
	private QueueProperties queueProperties;

	@Mock
	private ExchangeProperties exchangeProperties;

	@InjectMocks
	private WelcomeCouponIssueProducer producer;

	@Test
	@DisplayName("웰컴 쿠폰 MQ 메시지 전송 성공")
	void send() {
		// given
		Long userId = 1L;
		Long couponId = 2L;
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusDays(7);

		WelcomeCouponIssueMessage message = new WelcomeCouponIssueMessage(userId, couponId, start, end);

		String exchange = "exchange.welcome";
		String routingKey = "queue.welcome";

		when(exchangeProperties.getWelcome()).thenReturn(exchange);
		when(queueProperties.getWelcome()).thenReturn(routingKey);

		// when
		producer.send(message);

		// then
		verify(exchangeProperties).getWelcome();
		verify(queueProperties).getWelcome();
		verify(rabbitTemplate).convertAndSend(exchange, routingKey, message);
	}
}