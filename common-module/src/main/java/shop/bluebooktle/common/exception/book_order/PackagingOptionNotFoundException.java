package shop.bluebooktle.common.exception.book_order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PackagingOptionNotFoundException extends ApplicationException {

	public PackagingOptionNotFoundException() {
		super(ErrorCode.G_PACKAGING_ALREADY_EXISTS);
	}

	public PackagingOptionNotFoundException(String message) {
		super(ErrorCode.G_PACKAGING_ALREADY_EXISTS, message);
	}

	public PackagingOptionNotFoundException(Throwable cause) {
		super(ErrorCode.G_PACKAGING_ALREADY_EXISTS, cause);
	}

	public PackagingOptionNotFoundException(String message, Throwable cause) {
		super(ErrorCode.G_PACKAGING_ALREADY_EXISTS, message, cause);
	}
}
