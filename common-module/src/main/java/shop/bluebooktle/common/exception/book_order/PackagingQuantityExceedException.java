package shop.bluebooktle.common.exception.book_order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PackagingQuantityExceedException extends ApplicationException {
	public PackagingQuantityExceedException() {
		super(ErrorCode.G_PACKAGING_QUANTITY_EXCEEDS_BOOK_ORDER);
	}

}
