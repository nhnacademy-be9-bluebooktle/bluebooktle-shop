package shop.bluebooktle.backend.coupon.mq.direct;

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
class DirectCouponIssueProducerTest {

	@Mock
	private RabbitTemplate rabbitTemplate;

	@Mock
	private QueueProperties queueProperties;

	@Mock
	private ExchangeProperties exchangeProperties;

	@InjectMocks
	private DirectCouponIssueProducer producer;

	@Test
	@DisplayName("Direct MQ 발송 성공")
	void send() {
		// given
		CouponIssueMessage message = new CouponIssueMessage(
			1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusDays(7)
		);

		String exchangeName = "direct.exchange";
		String queueName = "direct.queue";

		when(exchangeProperties.getDirect()).thenReturn(exchangeName);
		when(queueProperties.getDirect()).thenReturn(queueName);

		// when
		producer.send(message);

		// then
		verify(exchangeProperties).getDirect();
		verify(queueProperties).getDirect();
		verify(rabbitTemplate).convertAndSend(exchangeName, queueName, message);
	}
}
