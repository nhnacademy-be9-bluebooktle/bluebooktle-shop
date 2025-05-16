package shop.bluebooktle.common.exception.auth;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class DormantAccountException extends ApplicationException {

	public DormantAccountException() {
		super(ErrorCode.AUTH_INACTIVE_ACCOUNT);
	}

	public DormantAccountException(String message) {
		super(ErrorCode.AUTH_INACTIVE_ACCOUNT, message);
	}

	public DormantAccountException(Throwable cause) {
		super(ErrorCode.AUTH_INACTIVE_ACCOUNT, cause);
	}

	public DormantAccountException(String message, Throwable cause) {
		super(ErrorCode.AUTH_INACTIVE_ACCOUNT, message, cause);
	}
}