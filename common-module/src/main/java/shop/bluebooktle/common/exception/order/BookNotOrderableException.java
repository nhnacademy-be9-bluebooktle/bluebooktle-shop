package shop.bluebooktle.common.exception.order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookNotOrderableException extends ApplicationException {
	public BookNotOrderableException() {
		super(ErrorCode.ORDER_BOOK_NOT_ORDERABLE);
	}

	public BookNotOrderableException(String message) {
		super(ErrorCode.ORDER_BOOK_NOT_ORDERABLE, message);
	}

	public BookNotOrderableException(Throwable cause) {
		super(ErrorCode.ORDER_BOOK_NOT_ORDERABLE, cause);
	}

	public BookNotOrderableException(String message, Throwable cause) {
		super(ErrorCode.ORDER_BOOK_NOT_ORDERABLE, message, cause);
	}
}
