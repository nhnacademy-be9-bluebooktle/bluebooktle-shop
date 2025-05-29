package shop.bluebooktle.backend.coupon.batch.direct;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.batch.direct.mq.DirectCouponIssueProducer;
import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class DirectCouponWriter implements ItemWriter<CouponIssueMessage> {

	private final DirectCouponIssueProducer directCouponIssueProducer;

	@Override
	public void write(Chunk<? extends CouponIssueMessage> chunk) {
		for (CouponIssueMessage message : chunk) {
			directCouponIssueProducer.send(message);
		}
		log.info("MQ로 {}개의 쿠폰 발급 메세지 전송", chunk.size());
	}
}
