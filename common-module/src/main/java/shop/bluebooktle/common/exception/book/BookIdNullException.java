package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookIdNullException extends ApplicationException {
	public BookIdNullException() {
		super(ErrorCode.BOOK_ID_NULL,
			ErrorCode.BOOK_ID_NULL.getMessage());
	}
}
