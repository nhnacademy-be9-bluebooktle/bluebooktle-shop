package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookAuthorNotFoundException extends ApplicationException {

	public BookAuthorNotFoundException() {
		super(ErrorCode.BOOK_AUTHOR_NOT_FOUND);
	}

	public BookAuthorNotFoundException(String message) {
		super(ErrorCode.BOOK_AUTHOR_NOT_FOUND, message);
	}

	public BookAuthorNotFoundException(Throwable cause) {
		super(ErrorCode.BOOK_AUTHOR_NOT_FOUND, cause);
	}

	public BookAuthorNotFoundException(Long bookId, Long authorId) {
		super(ErrorCode.BOOK_AUTHOR_NOT_FOUND,
			String.format("No link found between Book(id=%d) and Author(id=%d)", bookId, authorId));
	}
}
