package shop.bluebooktle.common.exception.book_order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookOrderNotFoundException extends ApplicationException {
	public BookOrderNotFoundException() {
		super(ErrorCode.G_BOOK_ORDER_NOT_FOUND);
	}

}
