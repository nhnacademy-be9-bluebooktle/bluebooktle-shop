package shop.bluebooktle.backend.payment.event.listener;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.payment.event.type.PaymentPointEarnEvent;
import shop.bluebooktle.backend.point.service.PointService;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentPointEarnEventListener {

	private final PointService pointService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlePointEarnEvent(PaymentPointEarnEvent event) {
		Long userId = event.userId();
		BigDecimal amount = event.amount();
		Long paymentId = event.paymentId();
		try {
			pointService.adjustUserPointAndSavePointHistory(userId, PointSourceTypeEnum.PAYMENT_EARN, amount,
				paymentId);
		} catch (Exception e) {
			log.error("결제 적립 포인트 지급 중 오류 발생 ID: {}. message: {}", userId, e.getMessage(), e);
		}
	}
}
