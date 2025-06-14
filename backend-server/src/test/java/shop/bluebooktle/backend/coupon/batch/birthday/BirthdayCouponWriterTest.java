package shop.bluebooktle.backend.coupon.batch.birthday;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.mq.birthday.BirthdayCouponIssueProducer;

@ExtendWith(MockitoExtension.class)
class BirthdayCouponWriterTest {

	@Mock
	private BirthdayCouponIssueProducer birthdayCouponIssueProducer;

	@InjectMocks
	private BirthdayCouponWriter birthdayCouponWriter;

	@Test
	@DisplayName("MQ로 쿠폰 발급 메시지 전송")
	void write() {
		// given
		CouponIssueMessage msg1 = new CouponIssueMessage(1L, 101L, LocalDateTime.now(),
			LocalDateTime.now().plusDays(1));
		CouponIssueMessage msg2 = new CouponIssueMessage(2L, 102L, LocalDateTime.now(),
			LocalDateTime.now().plusDays(1));
		Chunk<CouponIssueMessage> chunk = new Chunk<>(List.of(msg1, msg2));

		// when
		birthdayCouponWriter.write(chunk);

		// then
		verify(birthdayCouponIssueProducer).send(msg1);
		verify(birthdayCouponIssueProducer).send(msg2);
		verifyNoMoreInteractions(birthdayCouponIssueProducer);
	}
}