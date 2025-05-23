package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class TagCreateException extends ApplicationException {
	public TagCreateException() {
		super(ErrorCode.TAG_CREATE_FAILED);
	}

	public TagCreateException(String message) {
		super(ErrorCode.TAG_CREATE_FAILED, ErrorCode.TAG_CREATE_FAILED.getMessage() + message);
	}

	public TagCreateException(Throwable cause) {
		super(ErrorCode.TAG_CREATE_FAILED, cause);
	}

	public TagCreateException(String message, Throwable cause) {
		super(ErrorCode.TAG_CREATE_FAILED, message, cause);
	}
}
