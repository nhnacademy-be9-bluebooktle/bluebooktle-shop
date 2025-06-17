package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.membership.MembershipNotFoundException;

class MembershipExceptionCombinedTest {

	@Test
	@DisplayName("MembershipNotFoundException 생성 및 필드 확인")
	void testMembershipNotFoundException() {
		// given
		String message = "회원 등급을 찾을 수 없습니다.";

		// when
		MembershipNotFoundException exception = new MembershipNotFoundException(message);

		// then
		assertThat(exception)
			.isInstanceOf(MembershipNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.USER_MEMBERSHIP_NOT_FOUNT);

		assertThat(exception.getMessage()).contains(message);
	}

}
