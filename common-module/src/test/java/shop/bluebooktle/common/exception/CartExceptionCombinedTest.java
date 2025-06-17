package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.cart.CartNotFoundException;
import shop.bluebooktle.common.exception.cart.GuestUserNotFoundException;

class CartExceptionCombinedTest {

	@Test
	@DisplayName("CartNotFoundException 테스트")
	void cartNotFoundException() {
		CartNotFoundException exception = new CartNotFoundException();

		assertThat(exception)
			.isInstanceOf(CartNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.CART_NOT_FOUND);
	}

	@Test
	@DisplayName("GuestUserNotFoundException 테스트")
	void guestUserNotFoundException() {
		String customMessage = "게스트 사용자 ID가 유효하지 않습니다.";
		GuestUserNotFoundException exception = new GuestUserNotFoundException(customMessage);

		assertThat(exception)
			.isInstanceOf(GuestUserNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.GUEST_USER_NOT_FOUND);
		assertThat(exception.getMessage())
			.contains(customMessage);
	}
}
