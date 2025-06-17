package shop.bluebooktle.common.exception.cart;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class GuestUserNotFoundException extends ApplicationException {

	public GuestUserNotFoundException(String message) {
		super(ErrorCode.GUEST_USER_NOT_FOUND, message);
	}
}
