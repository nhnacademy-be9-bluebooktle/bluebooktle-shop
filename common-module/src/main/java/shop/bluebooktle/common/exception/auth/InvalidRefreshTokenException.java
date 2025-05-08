package shop.bluebooktle.common.exception.auth;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class InvalidRefreshTokenException extends ApplicationException {

	public InvalidRefreshTokenException() {
		super(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
	}

	public InvalidRefreshTokenException(String message) {
		super(ErrorCode.AUTH_INVALID_REFRESH_TOKEN, message);
	}

	public InvalidRefreshTokenException(Throwable cause) {
		super(ErrorCode.AUTH_INVALID_REFRESH_TOKEN, cause);
	}

	public InvalidRefreshTokenException(String message, Throwable cause) {
		super(ErrorCode.AUTH_INVALID_REFRESH_TOKEN, message, cause);
	}
}