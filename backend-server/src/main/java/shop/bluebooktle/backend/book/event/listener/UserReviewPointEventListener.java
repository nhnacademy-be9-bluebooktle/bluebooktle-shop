package shop.bluebooktle.backend.book.event.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.event.type.UserReviewPointEvent;
import shop.bluebooktle.backend.point.service.PointService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserReviewPointEventListener {

	private final PointService pointService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleUserLogin(UserReviewPointEvent event) {
		Long userId = event.userId();

		try {
			pointService.reviewPoint(userId);
		} catch (Exception e) {
			log.error("리뷰 포인트 지급 중 오류 발생 ID: {}. message: {}", userId, e.getMessage(), e);
		}

	}
}
