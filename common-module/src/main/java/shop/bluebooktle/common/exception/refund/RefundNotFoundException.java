package shop.bluebooktle.common.exception.refund;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class RefundNotFoundException extends ApplicationException {
	public RefundNotFoundException() {
		super(ErrorCode.REFUND_NOT_FOUND);
	}

	public RefundNotFoundException(Throwable cause) {
		super(ErrorCode.REFUND_NOT_FOUND, cause);
	}

	public RefundNotFoundException(String customMessage) {
		super(ErrorCode.REFUND_NOT_FOUND, customMessage);
	}

	public RefundNotFoundException(String customMessage, Throwable cause) {
		super(ErrorCode.REFUND_NOT_FOUND, customMessage, cause);
	}
}