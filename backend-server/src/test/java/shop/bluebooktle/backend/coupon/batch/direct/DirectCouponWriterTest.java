package shop.bluebooktle.backend.coupon.batch.direct;

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
import shop.bluebooktle.backend.coupon.mq.direct.DirectCouponIssueProducer;

@ExtendWith(MockitoExtension.class)
class DirectCouponWriterTest {

	@Mock
	private DirectCouponIssueProducer directCouponIssueProducer;

	@InjectMocks
	private DirectCouponWriter directCouponWriter;

	@Test
	@DisplayName("쿠폰 메시지 MQ 전송")
	void write() {
		// given
		CouponIssueMessage msg1 = new CouponIssueMessage(1L, 101L, LocalDateTime.now(),
			LocalDateTime.now().plusDays(7));
		CouponIssueMessage msg2 = new CouponIssueMessage(2L, 102L, LocalDateTime.now(),
			LocalDateTime.now().plusDays(7));
		Chunk<CouponIssueMessage> chunk = new Chunk<>(List.of(msg1, msg2));

		// when
		directCouponWriter.write(chunk);

		// then
		verify(directCouponIssueProducer).send(msg1);
		verify(directCouponIssueProducer).send(msg2);
		verifyNoMoreInteractions(directCouponIssueProducer);
	}
}