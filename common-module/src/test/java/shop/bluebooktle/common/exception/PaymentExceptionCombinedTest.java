package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.payment.PaymentNotFoundException;
import shop.bluebooktle.common.exception.payment.PaymentTypeAlreadyExistException;
import shop.bluebooktle.common.exception.payment.PaymentTypeNotFoundException;

class PaymentExceptionCombinedTest {

	@Test
	@DisplayName("PaymentNotFoundException 테스트")
	void paymentNotFoundException() {
		PaymentNotFoundException exception = new PaymentNotFoundException();

		assertThat(exception)
			.isInstanceOf(PaymentNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.PAYMENT_NOT_FOUND);
	}

	@Test
	@DisplayName("PaymentTypeAlreadyExistException 테스트")
	void paymentTypeAlreadyExistException() {
		PaymentTypeAlreadyExistException exception = new PaymentTypeAlreadyExistException();

		assertThat(exception)
			.isInstanceOf(PaymentTypeAlreadyExistException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.PAYMENT_TYPE_ALREADY_EXISTS);
	}

	@Test
	@DisplayName("PaymentTypeNotFoundException 테스트")
	void paymentTypeNotFoundException() {
		PaymentTypeNotFoundException exception = new PaymentTypeNotFoundException();

		assertThat(exception)
			.isInstanceOf(PaymentTypeNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.PAYMENT_TYPE_NOT_FOUND);
	}

}
