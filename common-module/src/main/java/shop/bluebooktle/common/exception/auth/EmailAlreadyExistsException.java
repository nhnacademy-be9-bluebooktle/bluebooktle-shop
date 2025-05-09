package shop.bluebooktle.common.exception.auth;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class EmailAlreadyExistsException extends ApplicationException {
	public EmailAlreadyExistsException() {
		super(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS);
	}

	public EmailAlreadyExistsException(String message) {
		super(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS, message);
	}

	public EmailAlreadyExistsException(Throwable cause) {
		super(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS, cause);
	}

	public EmailAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS, message, cause);
	}
}