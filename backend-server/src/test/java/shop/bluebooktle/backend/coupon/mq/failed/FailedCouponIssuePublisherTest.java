package shop.bluebooktle.backend.coupon.mq.failed;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.entity.FailedCouponIssue;
import shop.bluebooktle.backend.coupon.mq.failure.FailedCouponIssuePublisher;
import shop.bluebooktle.backend.coupon.mq.properties.ExchangeProperties;
import shop.bluebooktle.backend.coupon.mq.properties.QueueProperties;
import shop.bluebooktle.common.domain.coupon.CouponIssueType;

@ExtendWith(MockitoExtension.class)
class FailedCouponIssuePublisherTest {

	@Mock
	private RabbitTemplate rabbitTemplate;

	@Mock
	private ExchangeProperties exchangeProperties;

	@Mock
	private QueueProperties queueProperties;

	@InjectMocks
	private FailedCouponIssuePublisher publisher;

	private final Long userId = 1L;
	private final Long couponId = 2L;
	private final LocalDateTime start = LocalDateTime.now();
	private final LocalDateTime end = start.plusDays(7);

	@Test
	@DisplayName("BIRTHDAY 타입 재전송")
	void resend_birthday() {
		// given
		FailedCouponIssue issue = createIssue(CouponIssueType.BIRTHDAY);
		when(exchangeProperties.getBirthday()).thenReturn("birthday-exchange");
		when(queueProperties.getBirthday()).thenReturn("birthday-routing");

		// when
		publisher.resend(issue);

		// then
		ArgumentCaptor<CouponIssueMessage> captor = ArgumentCaptor.forClass(CouponIssueMessage.class);
		verify(rabbitTemplate).convertAndSend(eq("birthday-exchange"), eq("birthday-routing"), captor.capture());

		CouponIssueMessage msg = captor.getValue();
		assertThat(msg.getUserId()).isEqualTo(userId);
		assertThat(msg.getCouponId()).isEqualTo(couponId);
		assertThat(msg.getAvailableStartAt()).isEqualTo(start);
		assertThat(msg.getAvailableEndAt()).isEqualTo(end);
	}

	@Test
	@DisplayName("WELCOME 타입 재전송")
	void resend_welcome() {
		FailedCouponIssue issue = createIssue(CouponIssueType.WELCOME);
		when(exchangeProperties.getWelcome()).thenReturn("welcome-exchange");
		when(queueProperties.getWelcome()).thenReturn("welcome-routing");

		publisher.resend(issue);

		verify(rabbitTemplate).convertAndSend(eq("welcome-exchange"), eq("welcome-routing"),
			any(CouponIssueMessage.class));
	}

	@Test
	@DisplayName("DIRECT 타입 재전송")
	void resend_direct() {
		FailedCouponIssue issue = createIssue(CouponIssueType.DIRECT);
		when(exchangeProperties.getDirect()).thenReturn("direct-exchange");
		when(queueProperties.getDirect()).thenReturn("direct-routing");

		publisher.resend(issue);

		verify(rabbitTemplate).convertAndSend(eq("direct-exchange"), eq("direct-routing"),
			any(CouponIssueMessage.class));
	}

	private FailedCouponIssue createIssue(CouponIssueType type) {
		return FailedCouponIssue.builder()
			.userId(userId)
			.couponId(couponId)
			.availableStartAt(start)
			.availableEndAt(end)
			.issueType(type)
			.build();
	}
}
