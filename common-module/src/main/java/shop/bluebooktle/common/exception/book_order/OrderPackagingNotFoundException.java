package shop.bluebooktle.common.exception.book_order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class OrderPackagingNotFoundException extends ApplicationException {
	public OrderPackagingNotFoundException() {
		super(ErrorCode.G_ORDER_PACKAGING_NOT_FOUND);
	}

	public OrderPackagingNotFoundException(String message) {
		super(ErrorCode.G_ORDER_PACKAGING_NOT_FOUND, message);
	}

	public OrderPackagingNotFoundException(Throwable cause) {
		super(ErrorCode.G_ORDER_PACKAGING_NOT_FOUND, cause);
	}

	public OrderPackagingNotFoundException(String message, Throwable cause) {
		super(ErrorCode.G_ORDER_PACKAGING_NOT_FOUND, message, cause);
	}
}
