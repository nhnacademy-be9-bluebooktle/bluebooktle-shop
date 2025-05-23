package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PublisherListFetchException extends ApplicationException {
	public PublisherListFetchException() {
		super(ErrorCode.PUBLISHER_LIST_FETCH_ERROR);
	}

	public PublisherListFetchException(String message) {
		super(ErrorCode.PUBLISHER_LIST_FETCH_ERROR, ErrorCode.PUBLISHER_LIST_FETCH_ERROR.getMessage() + message);
	}

	public PublisherListFetchException(Throwable cause) {
		super(ErrorCode.PUBLISHER_LIST_FETCH_ERROR, cause);
	}

	public PublisherListFetchException(String message, Throwable cause) {
		super(ErrorCode.PUBLISHER_LIST_FETCH_ERROR, message, cause);
	}
}
