package shop.bluebooktle.common.exception.user;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AddressNotFoundException extends ApplicationException {
	public AddressNotFoundException() {
		super(ErrorCode.AUTH_ADDRESS_NOT_FOUND);
	}

	public AddressNotFoundException(String message) {
		super(ErrorCode.AUTH_ADDRESS_NOT_FOUND, message);
	}

	public AddressNotFoundException(Throwable cause) {
		super(ErrorCode.AUTH_ADDRESS_NOT_FOUND, cause);
	}

	public AddressNotFoundException(String message, Throwable cause) {
		super(ErrorCode.AUTH_ADDRESS_NOT_FOUND, message, cause);
	}

}


