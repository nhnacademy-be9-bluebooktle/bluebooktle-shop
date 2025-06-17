package shop.bluebooktle.common.exception.refund;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class RefundNotPossibleException extends ApplicationException {
	public RefundNotPossibleException() {
		super(ErrorCode.REFUND_NOT_POSSIBLE);
	}

	public RefundNotPossibleException(String customMessage) {
		super(ErrorCode.REFUND_NOT_POSSIBLE, customMessage);
	}

}