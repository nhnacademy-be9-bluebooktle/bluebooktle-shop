package shop.bluebooktle.common.exception.auth;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AuthenticationFailedException extends ApplicationException {

	public AuthenticationFailedException() {
		super(ErrorCode.AUTH_AUTHENTICATION_FAILED);
	}

	public AuthenticationFailedException(String message) {
		super(ErrorCode.AUTH_AUTHENTICATION_FAILED, message);
	}

	public AuthenticationFailedException(Throwable cause) {
		super(ErrorCode.AUTH_AUTHENTICATION_FAILED, cause);
	}

	public AuthenticationFailedException(String message, Throwable cause) {
		super(ErrorCode.AUTH_AUTHENTICATION_FAILED, message, cause);
	}
}