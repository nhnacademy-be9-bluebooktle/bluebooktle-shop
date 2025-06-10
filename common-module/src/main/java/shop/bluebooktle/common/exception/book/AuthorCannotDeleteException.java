package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AuthorCannotDeleteException extends ApplicationException {
	public AuthorCannotDeleteException() {
		super(ErrorCode.AUTHOR_DELETE_NOT_AVAILABLE);
	}

	public AuthorCannotDeleteException(String message) {
		super(ErrorCode.AUTHOR_DELETE_NOT_AVAILABLE, message);
	}

	public AuthorCannotDeleteException(Throwable cause) {
		super(ErrorCode.AUTHOR_DELETE_NOT_AVAILABLE, cause);
	}

	public AuthorCannotDeleteException(String message, Throwable cause) {
		super(ErrorCode.AUTHOR_DELETE_NOT_AVAILABLE, message, cause);
	}
}
