package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class TagAlreadyExistsException extends ApplicationException {

	public TagAlreadyExistsException() {
		super(ErrorCode.TAG_ALREADY_EXISTS);
	}

	public TagAlreadyExistsException(String message) {
		super(ErrorCode.TAG_ALREADY_EXISTS, message);
	}

	public TagAlreadyExistsException(Throwable cause) {
		super(ErrorCode.TAG_ALREADY_EXISTS, cause);
	}

	public TagAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.TAG_ALREADY_EXISTS, message, cause);
	}
}
