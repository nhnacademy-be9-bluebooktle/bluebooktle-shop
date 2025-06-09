package shop.bluebooktle.common.exception.order;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class StockNotEnoughException extends ApplicationException {
	public StockNotEnoughException() {
		super(ErrorCode.ORDER_STOCK_NOT_ENOUGH);
	}

	public StockNotEnoughException(String message) {
		super(ErrorCode.ORDER_STOCK_NOT_ENOUGH, message);
	}

	public StockNotEnoughException(Throwable cause) {
		super(ErrorCode.ORDER_STOCK_NOT_ENOUGH, cause);
	}

	public StockNotEnoughException(String message, Throwable cause) {
		super(ErrorCode.ORDER_STOCK_NOT_ENOUGH, message, cause);
	}
}
