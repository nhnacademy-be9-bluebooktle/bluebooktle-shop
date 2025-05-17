package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookAuthorAlreadyExistsException extends ApplicationException {
	public BookAuthorAlreadyExistsException(Long bookId, Long authorId) {
		super(ErrorCode.BOOK_AUTHOR_ALREADY_EXISTS,
			String.format("Book(id=%d) is already linked to Author(id=%d)", bookId, authorId));
	}
}
