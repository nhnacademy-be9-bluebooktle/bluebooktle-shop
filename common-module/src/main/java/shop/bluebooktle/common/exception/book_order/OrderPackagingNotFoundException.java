package shop.bluebooktle.common.exception.book_order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class OrderPackagingNotFoundException extends ApplicationException {
	public OrderPackagingNotFoundException() {
		super(ErrorCode.G_ORDER_PACKAGING_NOT_FOUND);
	}

}
