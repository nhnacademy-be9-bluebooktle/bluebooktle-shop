package shop.bluebooktle.common.exception.payment;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PaymentTypeNotFoundException extends ApplicationException {

	public PaymentTypeNotFoundException() {
		super(ErrorCode.PAYMENT_TYPE_NOT_FOUND);
	}

}
