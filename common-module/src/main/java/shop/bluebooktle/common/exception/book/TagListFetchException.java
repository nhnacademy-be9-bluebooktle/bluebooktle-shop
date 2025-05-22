package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class TagListFetchException extends ApplicationException {
	public TagListFetchException() {
		super(ErrorCode.TAG_LIST_FETCH_ERROR);
	}

	public TagListFetchException(String message) {
		super(ErrorCode.TAG_LIST_FETCH_ERROR, ErrorCode.TAG_LIST_FETCH_ERROR.getMessage() + message);
	}

	public TagListFetchException(Throwable cause) {
		super(ErrorCode.TAG_LIST_FETCH_ERROR, cause);
	}

	public TagListFetchException(String message, Throwable cause) {
		super(ErrorCode.TAG_LIST_FETCH_ERROR, message, cause);
	}
}
