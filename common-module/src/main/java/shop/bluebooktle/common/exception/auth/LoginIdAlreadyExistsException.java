package shop.bluebooktle.common.exception.auth;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class LoginIdAlreadyExistsException extends ApplicationException {

	public LoginIdAlreadyExistsException() {
		super(ErrorCode.AUTH_LOGIN_ID_ALREADY_EXISTS);
	}

	public LoginIdAlreadyExistsException(String message) {
		super(ErrorCode.AUTH_LOGIN_ID_ALREADY_EXISTS, message);
	}

	public LoginIdAlreadyExistsException(Throwable cause) {
		super(ErrorCode.AUTH_LOGIN_ID_ALREADY_EXISTS, cause);
	}

	public LoginIdAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.AUTH_LOGIN_ID_ALREADY_EXISTS, message, cause);
	}
}