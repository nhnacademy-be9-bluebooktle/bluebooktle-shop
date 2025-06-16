package shop.bluebooktle.auth.event.listener;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.auth.event.type.UserLoginEvent;
import shop.bluebooktle.auth.service.PointService;

@ExtendWith(MockitoExtension.class)
class LoginEventListenerTest {

	@Mock
	PointService pointService;

	@InjectMocks
	LoginEventListener loginEventListener;

	@Test
	@DisplayName("로그인 이벤트 처리 - 포인트 지급 성공")
	void handleUserLogin_success() {
		// given
		Long userId = 42L;
		UserLoginEvent event = new UserLoginEvent(userId);

		// when
		loginEventListener.handleUserLogin(event);

		// then
		verify(pointService).loginPoint(userId);
	}

	@Test
	@DisplayName("로그인 이벤트 처리 - 포인트 지급 중 예외 발생")
	void handleUserLogin_withException() {
		// given
		Long userId = 99L;
		UserLoginEvent event = new UserLoginEvent(userId);

		doThrow(new RuntimeException("포인트 오류"))
			.when(pointService).loginPoint(userId);

		// when
		loginEventListener.handleUserLogin(event);

		// then
		verify(pointService).loginPoint(userId);
		// 예외는 내부 catch로 처리되므로 테스트는 터지지 않음
	}
}
