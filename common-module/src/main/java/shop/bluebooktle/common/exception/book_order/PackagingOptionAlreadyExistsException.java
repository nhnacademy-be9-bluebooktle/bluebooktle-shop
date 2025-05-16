package shop.bluebooktle.common.exception.book_order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PackagingOptionAlreadyExistsException extends ApplicationException {
	public PackagingOptionAlreadyExistsException() {
		super(ErrorCode.G_ORDER_PACKAGING_OPTION_NOT_FOUND);
	}

	public PackagingOptionAlreadyExistsException(String message) {
		super(ErrorCode.G_ORDER_PACKAGING_OPTION_NOT_FOUND, message);
	}

	public PackagingOptionAlreadyExistsException(Throwable cause) {
		super(ErrorCode.G_ORDER_PACKAGING_OPTION_NOT_FOUND, cause);
	}

	public PackagingOptionAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.G_ORDER_PACKAGING_OPTION_NOT_FOUND, message, cause);
	}
}
