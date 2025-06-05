package shop.bluebooktle.auth.event.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.auth.event.type.UserSignUpEvent;
import shop.bluebooktle.auth.service.PointService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignupEventListener {

	private final PointService pointService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleUserSignup(UserSignUpEvent event) {
		Long userId = event.userId();

		try {
			pointService.signUpPoint(userId);
		} catch (Exception e) {
			log.error("회원가입 포인트 지급 중 오류 발생 ID: {}. message: {}", userId, e.getMessage(), e);
		}

	}
}