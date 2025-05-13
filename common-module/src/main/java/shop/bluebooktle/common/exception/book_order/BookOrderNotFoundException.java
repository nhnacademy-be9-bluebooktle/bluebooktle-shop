package shop.bluebooktle.common.exception.book_order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookOrderNotFoundException extends ApplicationException {
	public BookOrderNotFoundException() {
		super(ErrorCode.G_BOOK_ORDER_NOT_FOUND);
	}

	public BookOrderNotFoundException(String message) {
		super(ErrorCode.G_BOOK_ORDER_NOT_FOUND, message);
	}

	public BookOrderNotFoundException(Throwable cause) {
		super(ErrorCode.G_BOOK_ORDER_NOT_FOUND, cause);
	}

	public BookOrderNotFoundException(String message, Throwable cause) {
		super(ErrorCode.G_BOOK_ORDER_NOT_FOUND, message, cause);
	}
}
