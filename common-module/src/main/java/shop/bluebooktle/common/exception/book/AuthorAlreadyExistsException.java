package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AuthorAlreadyExistsException extends ApplicationException {

	public AuthorAlreadyExistsException() {
		super(ErrorCode.AUTHOR_ALREADY_EXISTS);
	}

	public AuthorAlreadyExistsException(String message) {
		super(ErrorCode.AUTHOR_ALREADY_EXISTS,
			ErrorCode.AUTHOR_ALREADY_EXISTS.getMessage() + "Author already exists with name: " + message
		);
	}

	public AuthorAlreadyExistsException(Throwable cause) {
		super(ErrorCode.AUTHOR_ALREADY_EXISTS, cause);
	}

	public AuthorAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.AUTHOR_ALREADY_EXISTS, message, cause);
	}
}