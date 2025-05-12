package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PublisherAlreadyExistsException extends ApplicationException {

	public PublisherAlreadyExistsException() {
		super(ErrorCode.PUBLISHER_ALREADY_EXISTS);
	}

	public PublisherAlreadyExistsException(String message) {
		super(ErrorCode.PUBLISHER_ALREADY_EXISTS, message);
	}

	public PublisherAlreadyExistsException(Throwable cause) {
		super(ErrorCode.PUBLISHER_ALREADY_EXISTS, cause);
	}

	public PublisherAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.PUBLISHER_ALREADY_EXISTS, message, cause);
	}
}
