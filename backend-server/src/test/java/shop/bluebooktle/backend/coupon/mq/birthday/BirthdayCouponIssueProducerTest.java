package shop.bluebooktle.backend.coupon.mq.birthday;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.mq.properties.ExchangeProperties;
import shop.bluebooktle.backend.coupon.mq.properties.QueueProperties;

@ExtendWith(MockitoExtension.class)
class BirthdayCouponIssueProducerTest {

	@Mock
	private RabbitTemplate rabbitTemplate;

	@Mock
	private QueueProperties queueProperties;

	@Mock
	private ExchangeProperties exchangeProperties;

	@InjectMocks
	private BirthdayCouponIssueProducer producer;

	@Test
	@DisplayName("생일 쿠폰 MQ 메시지 전송 성공")
	void send() {
		// given
		Long userId = 1L;
		Long couponId = 2L;
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusDays(7);

		CouponIssueMessage message = new CouponIssueMessage(userId, couponId, start, end);
		String exchange = "exchange.birthday";
		String routingKey = "queue.birthday";

		when(exchangeProperties.getBirthday()).thenReturn(exchange);
		when(queueProperties.getBirthday()).thenReturn(routingKey);

		// when
		producer.send(message);

		// then
		verify(exchangeProperties).getBirthday();
		verify(queueProperties).getBirthday();
		verify(rabbitTemplate).convertAndSend(exchange, routingKey, message);
	}
}