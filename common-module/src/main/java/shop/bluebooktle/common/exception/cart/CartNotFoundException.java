package shop.bluebooktle.common.exception.cart;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CartNotFoundException extends ApplicationException {
	public CartNotFoundException() {
		super(ErrorCode.CART_NOT_FOUND);
	}

	public CartNotFoundException(String message) {
		super(ErrorCode.CART_NOT_FOUND, message);
	}
}
