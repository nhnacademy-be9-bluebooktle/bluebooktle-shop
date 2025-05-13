package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AuthorNameEmptyException extends ApplicationException {

	public AuthorNameEmptyException() {
		super(ErrorCode.AUTHOR_NAME_EMPTY);
	}

	public AuthorNameEmptyException(String message) {
		super(ErrorCode.AUTHOR_NAME_EMPTY, message);
	}

	public AuthorNameEmptyException(Throwable cause) {
		super(ErrorCode.AUTHOR_NAME_EMPTY, cause);
	}

	public AuthorNameEmptyException(String message, Throwable cause) {
		super(ErrorCode.AUTHOR_NAME_EMPTY, message, cause);
	}
}
