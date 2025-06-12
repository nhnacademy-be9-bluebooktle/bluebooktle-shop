package shop.bluebooktle.common.exception.auth;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class HandleAccessDeniedException extends ApplicationException {

	public HandleAccessDeniedException() {
		super(ErrorCode.HANDLE_ACCESS_DENIED);
	}

	public HandleAccessDeniedException(String message) {
		super(ErrorCode.HANDLE_ACCESS_DENIED, message);
	}

	public HandleAccessDeniedException(Throwable cause) {
		super(ErrorCode.HANDLE_ACCESS_DENIED, cause);
	}

	public HandleAccessDeniedException(String message, Throwable cause) {
		super(ErrorCode.HANDLE_ACCESS_DENIED, message, cause);
	}
}