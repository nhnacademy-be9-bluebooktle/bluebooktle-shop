package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class TagNotFoundException extends ApplicationException {

	public TagNotFoundException() {
		super(ErrorCode.TAG_NOT_FOUND);
	}

	public TagNotFoundException(String message) {
		super(ErrorCode.TAG_NOT_FOUND, message);
	}

	public TagNotFoundException(Throwable cause) {
		super(ErrorCode.TAG_NOT_FOUND, cause);
	}

	public TagNotFoundException(String message, Throwable cause) {
		super(ErrorCode.TAG_NOT_FOUND, message, cause);
	}

	public TagNotFoundException(Long tagId) {
		this("Publisher with id " + tagId + " not found");
	}
}
