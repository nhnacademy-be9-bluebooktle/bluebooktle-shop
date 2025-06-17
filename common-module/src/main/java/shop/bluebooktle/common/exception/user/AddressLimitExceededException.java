package shop.bluebooktle.common.exception.user;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AddressLimitExceededException extends ApplicationException {

	public AddressLimitExceededException(String message) {
		super(ErrorCode.AUTH_ADDRESS_LIMIT_EXCEEDED, message);
	}

}


