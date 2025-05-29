package shop.bluebooktle.common.exception.user;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AddressNotFoundException extends ApplicationException {
	public AddressNotFoundException() {
		super(ErrorCode.AUTH_ADDRESS_LIMIT_EXCEEDED);
	}

	public AddressNotFoundException(String message) {
		super(ErrorCode.AUTH_ADDRESS_LIMIT_EXCEEDED, message);
	}

	public AddressNotFoundException(Throwable cause) {
		super(ErrorCode.AUTH_ADDRESS_LIMIT_EXCEEDED, cause);
	}

	public AddressNotFoundException(String message, Throwable cause) {
		super(ErrorCode.AUTH_ADDRESS_LIMIT_EXCEEDED, message, cause);
	}

}


