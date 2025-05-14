package shop.bluebooktle.common.exception.book_order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PackagingQuantityExceedException extends ApplicationException {
	public PackagingQuantityExceedException() {
		super(ErrorCode.G_PACKAGING_QUANTITY_EXCEEDS_BOOK_ORDER);
	}

	public PackagingQuantityExceedException(String message) {
		super(ErrorCode.G_PACKAGING_QUANTITY_EXCEEDS_BOOK_ORDER, message);
	}

	public PackagingQuantityExceedException(Throwable cause) {
		super(ErrorCode.G_PACKAGING_QUANTITY_EXCEEDS_BOOK_ORDER, cause);
	}

	public PackagingQuantityExceedException(String message, Throwable cause) {
		super(ErrorCode.G_PACKAGING_QUANTITY_EXCEEDS_BOOK_ORDER, message, cause);
	}
}
