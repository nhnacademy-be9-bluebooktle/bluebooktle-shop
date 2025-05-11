package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CategoryAlreadyExistsException extends ApplicationException {

	public CategoryAlreadyExistsException() {
		super(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS);
	}

	public CategoryAlreadyExistsException(String message) {
		super(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS, message);
	}

	public CategoryAlreadyExistsException(Throwable cause) {
		super(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS, cause);
	}

	public CategoryAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS, message, cause);
	}
}
