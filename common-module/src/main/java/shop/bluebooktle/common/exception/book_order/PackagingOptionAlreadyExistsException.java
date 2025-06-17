package shop.bluebooktle.common.exception.book_order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PackagingOptionAlreadyExistsException extends ApplicationException {
	public PackagingOptionAlreadyExistsException() {
		super(ErrorCode.G_ORDER_PACKAGING_OPTION_ALREADY_EXITS);
	}

}
