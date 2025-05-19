package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CategoryCannotDeleteRootException extends ApplicationException {

	public CategoryCannotDeleteRootException() {
		super(ErrorCode.CATEGORY_DELETE_NOT_ALLOWED);
	}

	public CategoryCannotDeleteRootException(String message) {
		super(ErrorCode.CATEGORY_DELETE_NOT_ALLOWED, ErrorCode.CATEGORY_DELETE_NOT_ALLOWED.getMessage() + message);
	}

	public CategoryCannotDeleteRootException(Throwable cause) {
		super(ErrorCode.CATEGORY_DELETE_NOT_ALLOWED, cause);
	}

	public CategoryCannotDeleteRootException(String message, Throwable cause) {
		super(ErrorCode.CATEGORY_DELETE_NOT_ALLOWED, message, cause);
	}
}
