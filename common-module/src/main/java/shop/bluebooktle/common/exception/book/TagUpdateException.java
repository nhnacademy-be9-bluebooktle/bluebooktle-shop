package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class TagUpdateException extends ApplicationException {
	public TagUpdateException() {
		super(ErrorCode.TAG_UPDATE_FAILED);
	}

	public TagUpdateException(String message) {
		super(ErrorCode.TAG_UPDATE_FAILED, ErrorCode.TAG_UPDATE_FAILED.getMessage() + message);
	}

	public TagUpdateException(Throwable cause) {
		super(ErrorCode.TAG_UPDATE_FAILED, cause);
	}

	public TagUpdateException(String message, Throwable cause) {
		super(ErrorCode.TAG_UPDATE_FAILED, message, cause);
	}
}
