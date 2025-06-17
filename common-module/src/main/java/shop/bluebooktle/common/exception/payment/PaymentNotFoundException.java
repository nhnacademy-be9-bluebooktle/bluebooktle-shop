package shop.bluebooktle.common.exception.payment;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PaymentNotFoundException extends ApplicationException {
	public PaymentNotFoundException() {
		super(ErrorCode.PAYMENT_NOT_FOUND);
	}

}
