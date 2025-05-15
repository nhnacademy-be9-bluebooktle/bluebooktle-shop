package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookAlreadyExistsException extends ApplicationException {

	public BookAlreadyExistsException() {
		super(ErrorCode.BOOK_ALREADY_EXISTS_EXCEPTION);
	}

	public BookAlreadyExistsException(String message) {
		super(ErrorCode.BOOK_ALREADY_EXISTS_EXCEPTION, message);
	}

	public BookAlreadyExistsException(Throwable cause) {
		super(ErrorCode.BOOK_ALREADY_EXISTS_EXCEPTION, cause);
	}

	public BookAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.BOOK_ALREADY_EXISTS_EXCEPTION, message, cause);
	}
}