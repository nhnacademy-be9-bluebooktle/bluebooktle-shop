package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AuthorIdNullException extends ApplicationException {

	public AuthorIdNullException() {
		super(ErrorCode.AUTHOR_ID_NULL);
	}

	public AuthorIdNullException(String message) {
		super(ErrorCode.AUTHOR_ID_NULL, message);
	}

	public AuthorIdNullException(Throwable cause) {
		super(ErrorCode.AUTHOR_ID_NULL, cause);
	}

	public AuthorIdNullException(String message, Throwable cause) {
		super(ErrorCode.AUTHOR_ID_NULL, message, cause);
	}

}
