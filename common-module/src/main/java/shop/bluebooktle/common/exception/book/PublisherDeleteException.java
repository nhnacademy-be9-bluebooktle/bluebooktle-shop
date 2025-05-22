package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PublisherDeleteException extends ApplicationException {
	public PublisherDeleteException() {
		super(ErrorCode.PUBLISHER_DELETE_FAILED);
	}

	public PublisherDeleteException(String message) {
		super(ErrorCode.PUBLISHER_DELETE_FAILED, ErrorCode.PUBLISHER_DELETE_FAILED.getMessage() + message);
	}

	public PublisherDeleteException(Throwable cause) {
		super(ErrorCode.PUBLISHER_DELETE_FAILED, cause);
	}

	public PublisherDeleteException(String message, Throwable cause) {
		super(ErrorCode.PUBLISHER_DELETE_FAILED, message, cause);
	}
}
