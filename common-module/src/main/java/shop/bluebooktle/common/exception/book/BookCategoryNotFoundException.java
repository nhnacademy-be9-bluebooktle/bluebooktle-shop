package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookCategoryNotFoundException extends ApplicationException {

	public BookCategoryNotFoundException() {
		super(ErrorCode.BOOK_CATEGORY_NOT_FOUND);
	}

	public BookCategoryNotFoundException(String message) {
		super(ErrorCode.BOOK_CATEGORY_NOT_FOUND, message);
	}

	public BookCategoryNotFoundException(Throwable cause) {
		super(ErrorCode.BOOK_CATEGORY_NOT_FOUND, cause);
	}

	public BookCategoryNotFoundException(Long bookId, Long categoryId) {
		this("Book category with id " + bookId + " does not exist in category " + categoryId);
	}
}
