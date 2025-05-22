package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class TagDeleteException extends ApplicationException {
	public TagDeleteException() {
		super(ErrorCode.TAG_DELETE_FAILED);
	}

	public TagDeleteException(String message) {
		super(ErrorCode.TAG_DELETE_FAILED, ErrorCode.TAG_DELETE_FAILED.getMessage() + message);
	}

	public TagDeleteException(Throwable cause) {
		super(ErrorCode.TAG_DELETE_FAILED, cause);
	}

	public TagDeleteException(String message, Throwable cause) {
		super(ErrorCode.TAG_DELETE_FAILED, message, cause);
	}
}
