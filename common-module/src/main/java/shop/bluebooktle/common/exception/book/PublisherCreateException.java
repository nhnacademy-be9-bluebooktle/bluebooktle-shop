package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PublisherCreateException extends ApplicationException {
	public PublisherCreateException() {
		super(ErrorCode.PUBLISHER_CREATE_FAILED);
	}

	public PublisherCreateException(String message) {
		super(ErrorCode.PUBLISHER_CREATE_FAILED, ErrorCode.PUBLISHER_CREATE_FAILED.getMessage() + message);
	}

	public PublisherCreateException(Throwable cause) {
		super(ErrorCode.PUBLISHER_CREATE_FAILED, cause);
	}

	public PublisherCreateException(String message, Throwable cause) {
		super(ErrorCode.PUBLISHER_CREATE_FAILED, message, cause);
	}
}
