package shop.bluebooktle.common.exception.payment;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PaymentTypeAlreadyExistException extends ApplicationException {

	public PaymentTypeAlreadyExistException() {
		super(ErrorCode.PAYMENT_TYPE_ALREADY_EXISTS);
	}

	public PaymentTypeAlreadyExistException(Throwable cause) {
		super(ErrorCode.PAYMENT_TYPE_ALREADY_EXISTS, cause);
	}

	public PaymentTypeAlreadyExistException(String customMessage) {
		super(ErrorCode.PAYMENT_TYPE_ALREADY_EXISTS, customMessage);
	}

	public PaymentTypeAlreadyExistException(String customMessage, Throwable cause) {
		super(ErrorCode.PAYMENT_TYPE_ALREADY_EXISTS, customMessage, cause);
	}
}
