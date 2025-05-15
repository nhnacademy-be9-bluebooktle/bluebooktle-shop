package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookSaleInfoAlreadyExistsException extends ApplicationException {

	public BookSaleInfoAlreadyExistsException() {
		super(ErrorCode.BOOK_SALE_INFO_ALREADY_EXISTS);
	}

	public BookSaleInfoAlreadyExistsException(String message) {
		super(ErrorCode.BOOK_SALE_INFO_ALREADY_EXISTS, message);
	}

	public BookSaleInfoAlreadyExistsException(Throwable cause) {
		super(ErrorCode.BOOK_SALE_INFO_ALREADY_EXISTS, cause);
	}

	public BookSaleInfoAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.BOOK_SALE_INFO_ALREADY_EXISTS, message, cause);
	}
}