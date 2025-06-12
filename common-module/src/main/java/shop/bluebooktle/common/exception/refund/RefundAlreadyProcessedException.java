package shop.bluebooktle.common.exception.refund;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class RefundAlreadyProcessedException extends ApplicationException {
	public RefundAlreadyProcessedException() {
		super(ErrorCode.REFUND_ALREADY_PROCESSED);
	}

	public RefundAlreadyProcessedException(Throwable cause) {
		super(ErrorCode.REFUND_ALREADY_PROCESSED, cause);
	}

	public RefundAlreadyProcessedException(String customMessage) {
		super(ErrorCode.REFUND_ALREADY_PROCESSED, customMessage);
	}

	public RefundAlreadyProcessedException(String customMessage, Throwable cause) {
		super(ErrorCode.REFUND_ALREADY_PROCESSED, customMessage, cause);
	}
}