package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PublisherCannotDeleteException extends ApplicationException {
	public PublisherCannotDeleteException() {
		super(ErrorCode.PUBLISHER_DELETE_NOT_AVAILABLE);
	}

	public PublisherCannotDeleteException(String message) {
		super(ErrorCode.PUBLISHER_DELETE_NOT_AVAILABLE, message);
	}

	public PublisherCannotDeleteException(Throwable cause) {
		super(ErrorCode.PUBLISHER_DELETE_NOT_AVAILABLE, cause);
	}

	public PublisherCannotDeleteException(String message, Throwable cause) {
		super(ErrorCode.PUBLISHER_DELETE_NOT_AVAILABLE, message, cause);
	}
}
