package shop.bluebooktle.common.exception.user;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class InvalidUserIdException extends ApplicationException {
	public InvalidUserIdException() {
		super(ErrorCode.AUTH_INVALID_USER_ID);
	}

	public InvalidUserIdException(String message) {
		super(ErrorCode.AUTH_INVALID_USER_ID, message);
	}

}


