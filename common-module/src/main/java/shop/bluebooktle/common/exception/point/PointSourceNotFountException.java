package shop.bluebooktle.common.exception.point;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PointSourceNotFountException extends ApplicationException {
	public PointSourceNotFountException() {
		super(ErrorCode.POINT_SOURCE_TYPE_NOT_FOUND);
	}

	public PointSourceNotFountException(Throwable cause) {
		super(ErrorCode.POINT_SOURCE_TYPE_NOT_FOUND, cause);
	}

	public PointSourceNotFountException(String customMessage) {
		super(ErrorCode.POINT_SOURCE_TYPE_NOT_FOUND, customMessage);
	}

	public PointSourceNotFountException(String customMessage, Throwable cause) {
		super(ErrorCode.POINT_SOURCE_TYPE_NOT_FOUND, customMessage, cause);
	}
}
