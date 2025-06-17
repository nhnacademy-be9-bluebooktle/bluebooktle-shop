package shop.bluebooktle.common.exception.user;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AddressNotFoundException extends ApplicationException {

	public AddressNotFoundException(String message) {
		super(ErrorCode.AUTH_ADDRESS_NOT_FOUND, message);
	}

}


