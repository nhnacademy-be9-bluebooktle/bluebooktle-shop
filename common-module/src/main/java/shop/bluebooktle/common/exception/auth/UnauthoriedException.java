package shop.bluebooktle.common.exception.auth;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class UnauthoriedException extends ApplicationException {

	public UnauthoriedException() {
		super(ErrorCode.UNAUTHORIZED);
	}

	public UnauthoriedException(String message) {
		super(ErrorCode.UNAUTHORIZED, message);
	}

	public UnauthoriedException(Throwable cause) {
		super(ErrorCode.UNAUTHORIZED, cause);
	}

	public UnauthoriedException(String message, Throwable cause) {
		super(ErrorCode.UNAUTHORIZED, message, cause);
	}
}