package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.user.AddressLimitExceededException;
import shop.bluebooktle.common.exception.user.AddressNotFoundException;
import shop.bluebooktle.common.exception.user.InvalidUserIdException;

class UserExceptionCombinedTest {

	@Test
	@DisplayName("AddressLimitExceededException 테스트")
	void addressLimitExceededException() {
		String message = "최대 주소 등록 개수를 초과했습니다.";
		AddressLimitExceededException exception = new AddressLimitExceededException(message);

		assertThat(exception)
			.isInstanceOf(AddressLimitExceededException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_ADDRESS_LIMIT_EXCEEDED);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("AddressNotFoundException 테스트")
	void addressNotFoundException() {
		String message = "주소를 찾을 수 없습니다.";
		AddressNotFoundException exception = new AddressNotFoundException(message);

		assertThat(exception)
			.isInstanceOf(AddressNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_ADDRESS_NOT_FOUND);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("InvalidUserIdException 기본 생성자 테스트")
	void invalidUserIdException_default() {
		InvalidUserIdException exception = new InvalidUserIdException();

		assertThat(exception)
			.isInstanceOf(InvalidUserIdException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_USER_ID);
	}

	@Test
	@DisplayName("InvalidUserIdException 커스텀 메시지 생성자 테스트")
	void invalidUserIdException_withMessage() {
		String message = "잘못된 사용자 ID입니다.";
		InvalidUserIdException exception = new InvalidUserIdException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_USER_ID);
		assertThat(exception.getMessage()).contains(message);
	}

}
