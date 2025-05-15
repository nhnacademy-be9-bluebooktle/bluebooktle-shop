package shop.bluebooktle.common.exception.payment;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PaymentDetailNotFoundException extends ApplicationException {
	public PaymentDetailNotFoundException() {
		super(ErrorCode.PAYMENT_DETAIL_NOT_FOUND);
	}

	public PaymentDetailNotFoundException(Throwable cause) {
		super(ErrorCode.PAYMENT_DETAIL_NOT_FOUND, cause);
	}

	public PaymentDetailNotFoundException(String customMessage) {
		super(ErrorCode.PAYMENT_DETAIL_NOT_FOUND, customMessage);
	}

	public PaymentDetailNotFoundException(String customMessage, Throwable cause) {
		super(ErrorCode.PAYMENT_DETAIL_NOT_FOUND, customMessage, cause);
	}
}
