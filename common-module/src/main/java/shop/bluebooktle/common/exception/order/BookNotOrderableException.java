package shop.bluebooktle.common.exception.order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookNotOrderableException extends ApplicationException {

	public BookNotOrderableException(String message) {
		super(ErrorCode.ORDER_BOOK_NOT_ORDERABLE, message);
	}

}
