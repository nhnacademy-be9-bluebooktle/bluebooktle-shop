package shop.bluebooktle.auth.event.listener;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.auth.event.type.UserSignUpEvent;
import shop.bluebooktle.auth.service.PointService;

@ExtendWith(MockitoExtension.class)
class SignupEventListenerTest {

	@Mock
	PointService pointService;

	@InjectMocks
	SignupEventListener signupEventListener;

	@Test
	@DisplayName("회원가입 이벤트 처리 - 포인트 지급 성공")
	void handleUserSignup_success() {
		// given
		Long userId = 1L;
		UserSignUpEvent event = new UserSignUpEvent(userId);

		// when
		signupEventListener.handleUserSignup(event);

		// then
		verify(pointService).signUpPoint(userId);
	}

	@Test
	@DisplayName("회원가입 이벤트 처리 - 포인트 지급 중 예외 발생")
	void handleUserSignup_withException() {
		// given
		Long userId = 2L;
		UserSignUpEvent event = new UserSignUpEvent(userId);

		doThrow(new RuntimeException("포인트 지급 실패"))
			.when(pointService).signUpPoint(userId);

		// when
		signupEventListener.handleUserSignup(event);

		// then
		verify(pointService).signUpPoint(userId);
		// 예외는 내부에서 catch되므로 테스트는 실패하지 않음
	}
}
