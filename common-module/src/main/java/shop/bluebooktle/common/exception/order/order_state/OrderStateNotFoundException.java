package shop.bluebooktle.common.exception.order.order_state;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class OrderStateNotFoundException extends ApplicationException {
	public OrderStateNotFoundException(String name) {
		super(ErrorCode.ORDER_STATE_NOT_FOUND, ErrorCode.ORDER_STATE_NOT_FOUND + " 상태명: " + name);
	}

	public OrderStateNotFoundException() {
		super(ErrorCode.ORDER_STATE_NOT_FOUND);
	}

}
