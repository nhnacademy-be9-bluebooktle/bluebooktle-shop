package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.refund.RefundAlreadyProcessedException;
import shop.bluebooktle.common.exception.refund.RefundNotFoundException;
import shop.bluebooktle.common.exception.refund.RefundNotPossibleException;

class RefundExceptionCombinedTest {

	@Test
	@DisplayName("RefundAlreadyProcessedException 테스트")
	void refundAlreadyProcessedException() {
		RefundAlreadyProcessedException exception = new RefundAlreadyProcessedException();

		assertThat(exception)
			.isInstanceOf(RefundAlreadyProcessedException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.REFUND_ALREADY_PROCESSED);
	}

	@Test
	@DisplayName("RefundNotFoundException 테스트")
	void refundNotFoundException() {
		RefundNotFoundException exception = new RefundNotFoundException();

		assertThat(exception)
			.isInstanceOf(RefundNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.REFUND_NOT_FOUND);
	}

	@Test
	@DisplayName("RefundNotPossibleException 기본 생성자 테스트")
	void refundNotPossibleDefault() {
		RefundNotPossibleException exception = new RefundNotPossibleException();

		assertThat(exception)
			.isInstanceOf(RefundNotPossibleException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.REFUND_NOT_POSSIBLE);
	}

	@Test
	@DisplayName("RefundNotPossibleException 커스텀 메시지 테스트")
	void refundNotPossibleWithMessage() {
		String message = "기간 초과로 환불이 불가능합니다.";
		RefundNotPossibleException exception = new RefundNotPossibleException(message);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.REFUND_NOT_POSSIBLE);
		assertThat(exception.getMessage()).contains(message);
	}

}
