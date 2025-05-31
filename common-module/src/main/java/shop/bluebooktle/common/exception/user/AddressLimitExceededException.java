package shop.bluebooktle.common.exception.user;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AddressLimitExceededException extends ApplicationException {
	public AddressLimitExceededException() {
		super(ErrorCode.AUTH_ADDRESS_LIMIT_EXCEEDED);
	}

	public AddressLimitExceededException(String message) {
		super(ErrorCode.AUTH_ADDRESS_LIMIT_EXCEEDED, message);
	}

	public AddressLimitExceededException(Throwable cause) {
		super(ErrorCode.AUTH_ADDRESS_LIMIT_EXCEEDED, cause);
	}

	public AddressLimitExceededException(String message, Throwable cause) {
		super(ErrorCode.AUTH_ADDRESS_LIMIT_EXCEEDED, message, cause);
	}

}


