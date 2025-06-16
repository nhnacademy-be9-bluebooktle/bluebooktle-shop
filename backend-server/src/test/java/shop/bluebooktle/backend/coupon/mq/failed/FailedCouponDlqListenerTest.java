package shop.bluebooktle.backend.coupon.mq.failed;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.entity.FailedCouponIssue;
import shop.bluebooktle.backend.coupon.mq.failure.FailedCouponDlqListener;
import shop.bluebooktle.backend.coupon.mq.properties.QueueProperties;
import shop.bluebooktle.backend.coupon.repository.FailedCouponIssueRepository;

@ExtendWith(MockitoExtension.class)
class FailedCouponDlqListenerTest {

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private FailedCouponIssueRepository failedCouponIssueRepository;

	@Mock
	private QueueProperties queueProperties;

	@InjectMocks
	private FailedCouponDlqListener listener;

	private final LocalDateTime now = LocalDateTime.now();

	@Test
	@DisplayName("정상 처리 - BIRTHDAY")
	void listen_success() throws Exception {
		// given
		CouponIssueMessage mockPayload = new CouponIssueMessage(1L, 2L, now, now.plusDays(3));
		byte[] body = "mocked-json-body".getBytes();
		MessageProperties properties = new MessageProperties();
		properties.setConsumerQueue("queue.birthday.dlq");

		Map<String, Object> death = Map.of("reason", "expired");
		properties.setHeader("x-death", List.of(death));

		Message message = new Message(body, properties);

		when(queueProperties.getBirthdayDlq()).thenReturn("queue.birthday.dlq");
		when(objectMapper.readValue(eq(body), eq(CouponIssueMessage.class))).thenReturn(mockPayload);

		// when
		listener.listen(message);

		// then
		verify(failedCouponIssueRepository).save(any(FailedCouponIssue.class));
	}

	@Test
	@DisplayName("지원하지 않는 큐는 저장하지 않음")
	void listen_invalidQueue() throws Exception {
		CouponIssueMessage payload = new CouponIssueMessage(1L, 2L, now, now.plusDays(1));
		byte[] body = "valid-body".getBytes();
		MessageProperties properties = new MessageProperties();
		properties.setConsumerQueue("unsupported.queue");
		Message message = new Message(body, properties);

		when(objectMapper.readValue(eq(body), eq(CouponIssueMessage.class))).thenReturn(payload);
		when(queueProperties.getBirthdayDlq()).thenReturn("birthday.dlq");
		when(queueProperties.getWelcomeDlq()).thenReturn("welcome.dlq");
		when(queueProperties.getDirectDlq()).thenReturn("direct.dlq");

		listener.listen(message);

		verify(failedCouponIssueRepository, never()).save(any());
	}

	@Test
	@DisplayName("x-death 헤더 없음 → reason: unknown 저장")
	void listen_missingXDeath() throws Exception {
		CouponIssueMessage payload = new CouponIssueMessage(1L, 2L, now, now.plusDays(2));
		byte[] body = "valid-body".getBytes();
		MessageProperties props = new MessageProperties();
		props.setConsumerQueue("queue.welcome.dlq");
		Message message = new Message(body, props);

		when(queueProperties.getWelcomeDlq()).thenReturn("queue.welcome.dlq");
		when(objectMapper.readValue(eq(body), eq(CouponIssueMessage.class))).thenReturn(payload);

		listener.listen(message);

		verify(failedCouponIssueRepository).save(any(FailedCouponIssue.class));
	}
}
