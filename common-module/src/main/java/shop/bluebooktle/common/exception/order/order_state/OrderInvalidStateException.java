package shop.bluebooktle.common.exception.order.order_state;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class OrderInvalidStateException extends ApplicationException {
	public OrderInvalidStateException(String name) {
		super(ErrorCode.ORDER_INVALID_STATE);
	}

	public OrderInvalidStateException() {
		super(ErrorCode.ORDER_INVALID_STATE);
	}

}
