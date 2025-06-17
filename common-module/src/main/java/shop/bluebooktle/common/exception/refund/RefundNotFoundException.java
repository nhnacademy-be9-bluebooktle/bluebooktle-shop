package shop.bluebooktle.common.exception.refund;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class RefundNotFoundException extends ApplicationException {
	public RefundNotFoundException() {
		super(ErrorCode.REFUND_NOT_FOUND);
	}

}