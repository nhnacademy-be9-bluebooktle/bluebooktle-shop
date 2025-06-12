package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class ReviewAuthorizationException extends ApplicationException {
	public ReviewAuthorizationException() {
		super(ErrorCode.REVIEW_AUTHORIZATION_FAILED);
	}

	public ReviewAuthorizationException(String message) {
		super(ErrorCode.REVIEW_AUTHORIZATION_FAILED,
			ErrorCode.REVIEW_AUTHORIZATION_FAILED.getMessage() + ": " + message);
	}

	public ReviewAuthorizationException(Throwable cause) {
		super(ErrorCode.REVIEW_AUTHORIZATION_FAILED, cause);
	}

	public ReviewAuthorizationException(String message, Throwable cause) {
		super(ErrorCode.REVIEW_AUTHORIZATION_FAILED,
			ErrorCode.REVIEW_AUTHORIZATION_FAILED.getMessage() + ": " + message, cause);
	}

	public ReviewAuthorizationException(ErrorCode errorCode) {
		super(errorCode);
	}
}