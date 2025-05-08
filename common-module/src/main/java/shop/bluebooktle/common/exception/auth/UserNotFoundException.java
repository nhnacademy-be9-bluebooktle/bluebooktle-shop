package shop.bluebooktle.common.exception.auth;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class UserNotFoundException extends ApplicationException {

	public UserNotFoundException() {
		super(ErrorCode.AUTH_USER_NOT_FOUND);
	}

	public UserNotFoundException(String message) {
		super(ErrorCode.AUTH_USER_NOT_FOUND, message);
	}

	public UserNotFoundException(Throwable cause) {
		super(ErrorCode.AUTH_USER_NOT_FOUND, cause);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(ErrorCode.AUTH_USER_NOT_FOUND, message, cause);
	}
}