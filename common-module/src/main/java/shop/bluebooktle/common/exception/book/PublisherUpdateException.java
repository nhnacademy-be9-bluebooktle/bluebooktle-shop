package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PublisherUpdateException extends ApplicationException {
	public PublisherUpdateException() {
		super(ErrorCode.TAG_UPDATE_FAILED);
	}

	public PublisherUpdateException(String message) {
		super(ErrorCode.TAG_UPDATE_FAILED, ErrorCode.TAG_UPDATE_FAILED.getMessage() + message);
	}

	public PublisherUpdateException(Throwable cause) {
		super(ErrorCode.TAG_UPDATE_FAILED, cause);
	}

	public PublisherUpdateException(String message, Throwable cause) {
		super(ErrorCode.TAG_UPDATE_FAILED, message, cause);
	}
}
