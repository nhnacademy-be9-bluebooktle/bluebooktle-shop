package shop.bluebooktle.common.exception.order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class OrderNotFoundException extends ApplicationException {
	public OrderNotFoundException() {
		super(ErrorCode.ORDER_NOT_FOUND);
	}

	public OrderNotFoundException(String message) {
		super(ErrorCode.ORDER_NOT_FOUND, message);
	}

	public OrderNotFoundException(Throwable cause) {
		super(ErrorCode.ORDER_NOT_FOUND, cause);
	}

	public OrderNotFoundException(String message, Throwable cause) {
		super(ErrorCode.ORDER_NOT_FOUND, message, cause);
	}
}
