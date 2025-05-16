package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookCategoryAlreadyExistsException extends ApplicationException {
	public BookCategoryAlreadyExistsException() {
		super(ErrorCode.BOOK_CATEGORY_ALREADY_EXISTS);
	}

	public BookCategoryAlreadyExistsException(String message) {
		super(ErrorCode.BOOK_CATEGORY_ALREADY_EXISTS, message);
	}

	public BookCategoryAlreadyExistsException(Throwable cause) {
		super(ErrorCode.BOOK_CATEGORY_ALREADY_EXISTS, cause);
	}

	public BookCategoryAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.BOOK_CATEGORY_ALREADY_EXISTS, message, cause);
	}

	public BookCategoryAlreadyExistsException(Long bookId, Long categoryId) {
		this("Book category with id " + bookId + " already exists in category " + categoryId);
	}
}
