package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookCategoryRequiredException extends ApplicationException {

	public BookCategoryRequiredException() {
		super(ErrorCode.BOOK_CATEGORY_REQUIRED);
	}

	public BookCategoryRequiredException(String message) {
		super(ErrorCode.BOOK_CATEGORY_REQUIRED, message);
	}

	public BookCategoryRequiredException(Throwable cause) {
		super(ErrorCode.BOOK_CATEGORY_REQUIRED, cause);
	}

	public BookCategoryRequiredException(Long bookId) {
		this("Book (id=" + bookId + ") must belong to at least one category.");
	}
}
