package shop.bluebooktle.common.exception.book_order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PackagingOptionNotFoundException extends ApplicationException {

	public PackagingOptionNotFoundException() {
		super(ErrorCode.G_ORDER_PACKAGING_OPTION_NOT_FOUND);
	}

}
