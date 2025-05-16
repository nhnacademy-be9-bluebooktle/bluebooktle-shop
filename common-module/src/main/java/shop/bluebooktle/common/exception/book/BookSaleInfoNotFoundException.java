package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookSaleInfoNotFoundException extends ApplicationException {

	public BookSaleInfoNotFoundException() {
		super(ErrorCode.BOOK_SALE_INFO_NOT_FOUND);
	}

	public BookSaleInfoNotFoundException(String message) {
		super(ErrorCode.BOOK_SALE_INFO_NOT_FOUND, message);
	}

	public BookSaleInfoNotFoundException(Throwable cause) {
		super(ErrorCode.BOOK_SALE_INFO_NOT_FOUND, cause);
	}

	public BookSaleInfoNotFoundException(String message, Throwable cause) {
		super(ErrorCode.BOOK_SALE_INFO_NOT_FOUND, message, cause);
	}
}