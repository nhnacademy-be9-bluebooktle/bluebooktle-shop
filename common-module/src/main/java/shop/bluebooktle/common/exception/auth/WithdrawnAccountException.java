package shop.bluebooktle.common.exception.auth;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class WithdrawnAccountException extends ApplicationException {

	public WithdrawnAccountException() {
		super(ErrorCode.AUTH_ACCOUNT_WITHDRAWN);
	}

	public WithdrawnAccountException(String message) {
		super(ErrorCode.AUTH_ACCOUNT_WITHDRAWN, message);
	}

	public WithdrawnAccountException(Throwable cause) {
		super(ErrorCode.AUTH_ACCOUNT_WITHDRAWN, cause);
	}

	public WithdrawnAccountException(String message, Throwable cause) {
		super(ErrorCode.AUTH_ACCOUNT_WITHDRAWN, message, cause);
	}
}