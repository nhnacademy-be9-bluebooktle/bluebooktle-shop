package shop.bluebooktle.common.exception.point;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PointInSufficeientBalance extends ApplicationException {
	public PointInSufficeientBalance() {
		super(ErrorCode.POINT_INSUFFICIENT_BALANCE);
	}

	public PointInSufficeientBalance(Throwable cause) {
		super(ErrorCode.POINT_INSUFFICIENT_BALANCE, cause);
	}

	public PointInSufficeientBalance(String customMessage) {
		super(ErrorCode.POINT_INSUFFICIENT_BALANCE, customMessage);
	}

	public PointInSufficeientBalance(String customMessage, Throwable cause) {
		super(ErrorCode.POINT_INSUFFICIENT_BALANCE, customMessage, cause);
	}
}