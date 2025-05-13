package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookCategoryLimitExceededException extends ApplicationException {

	public BookCategoryLimitExceededException() {
		super(ErrorCode.BOOK_CATEGORY_LIMIT_EXCEEDED);
	}

	public BookCategoryLimitExceededException(String message) {
		super(ErrorCode.BOOK_CATEGORY_LIMIT_EXCEEDED, message);
	}

	public BookCategoryLimitExceededException(Throwable cause) {
		super(ErrorCode.BOOK_CATEGORY_LIMIT_EXCEEDED, cause);
	}

	public BookCategoryLimitExceededException(String message, Throwable cause) {
		super(ErrorCode.BOOK_CATEGORY_LIMIT_EXCEEDED, message, cause);
	}

	public BookCategoryLimitExceededException(Long bookId) {
		this("Book(id=" + bookId + ") can have at most 10 categories.");
	}
}
