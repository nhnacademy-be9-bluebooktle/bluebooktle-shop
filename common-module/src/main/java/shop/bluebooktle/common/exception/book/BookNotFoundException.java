package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookNotFoundException extends ApplicationException {
	public BookNotFoundException() {
		super(ErrorCode.BOOK_NOT_FOUND);
	}

	public BookNotFoundException(String message) {
		super(ErrorCode.BOOK_NOT_FOUND, message);
	}

	public BookNotFoundException(Throwable cause) {
		super(ErrorCode.BOOK_NOT_FOUND, cause);
	}

	public BookNotFoundException(String message, Throwable cause) {
		super(ErrorCode.BOOK_NOT_FOUND, message, cause);
	}
}
