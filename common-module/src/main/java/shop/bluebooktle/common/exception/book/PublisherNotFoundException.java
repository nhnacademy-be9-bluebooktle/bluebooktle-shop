package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PublisherNotFoundException extends ApplicationException {

	public PublisherNotFoundException() {
		super(ErrorCode.PUBLISHER_NOT_FOUND);
	}

	public PublisherNotFoundException(String message) {
		super(ErrorCode.PUBLISHER_NOT_FOUND, message);
	}

	public PublisherNotFoundException(Throwable cause) {
		super(ErrorCode.PUBLISHER_NOT_FOUND, cause);
	}

	public PublisherNotFoundException(String message, Throwable cause) {
		super(ErrorCode.PUBLISHER_NOT_FOUND, message, cause);
	}

	public PublisherNotFoundException(Long publisherId) {
		this("Publisher with id " + publisherId + " not found");
	}
}
