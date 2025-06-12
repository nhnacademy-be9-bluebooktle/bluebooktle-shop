package shop.bluebooktle.common.exception.payment;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PaymentNotFoundException extends ApplicationException {
	public PaymentNotFoundException() {
		super(ErrorCode.PAYMENT_NOT_FOUND);
	}

	public PaymentNotFoundException(Throwable cause) {
		super(ErrorCode.PAYMENT_NOT_FOUND, cause);
	}

	public PaymentNotFoundException(String customMessage) {
		super(ErrorCode.PAYMENT_NOT_FOUND, customMessage);
	}

	public PaymentNotFoundException(String customMessage, Throwable cause) {
		super(ErrorCode.PAYMENT_NOT_FOUND, customMessage, cause);
	}
}
