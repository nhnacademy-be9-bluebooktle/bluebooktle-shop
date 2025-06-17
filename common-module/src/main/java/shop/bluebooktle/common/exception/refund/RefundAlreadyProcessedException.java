package shop.bluebooktle.common.exception.refund;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class RefundAlreadyProcessedException extends ApplicationException {
	public RefundAlreadyProcessedException() {
		super(ErrorCode.REFUND_ALREADY_PROCESSED);
	}

}