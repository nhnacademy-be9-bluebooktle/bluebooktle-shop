package shop.bluebooktle.backend.coupon.batch.birthday;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.dto.CouponIssueMessage;
import shop.bluebooktle.backend.coupon.mq.birthday.BirthdayCouponIssueProducer;

@Component
@RequiredArgsConstructor
@Slf4j
public class BirthdayCouponWriter implements ItemWriter<CouponIssueMessage> {

	private final BirthdayCouponIssueProducer birthdayCouponIssueProducer;

	@Override
	public void write(Chunk<? extends CouponIssueMessage> chunk) {
		for (CouponIssueMessage message : chunk) {
			birthdayCouponIssueProducer.send(message);
		}
		log.info("MQ로 {}개의 쿠폰 발급 메세지 전송", chunk.size());
	}
}