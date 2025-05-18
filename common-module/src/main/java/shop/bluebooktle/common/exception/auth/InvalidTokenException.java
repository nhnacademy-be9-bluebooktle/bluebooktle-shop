package shop.bluebooktle.common.exception.auth;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class InvalidTokenException extends ApplicationException {

	public InvalidTokenException() {
		super(ErrorCode.AUTH_TOKEN_VALIDATION_FAILED);
	}

	public InvalidTokenException(String message) {
		super(ErrorCode.AUTH_TOKEN_VALIDATION_FAILED, message);
	}

	public InvalidTokenException(Throwable cause) {
		super(ErrorCode.AUTH_TOKEN_VALIDATION_FAILED, cause);
	}

	public InvalidTokenException(String message, Throwable cause) {
		super(ErrorCode.AUTH_TOKEN_VALIDATION_FAILED, message, cause);
	}
}