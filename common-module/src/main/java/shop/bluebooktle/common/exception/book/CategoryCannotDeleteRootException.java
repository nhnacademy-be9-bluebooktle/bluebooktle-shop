package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CategoryCannotDeleteRootException extends ApplicationException {

	public CategoryCannotDeleteRootException() {
		super(ErrorCode.CATEGORY_DELETE_ROOT_CATEGORY);
	}

	public CategoryCannotDeleteRootException(String message) {
		super(ErrorCode.CATEGORY_DELETE_ROOT_CATEGORY, message);
	}

	public CategoryCannotDeleteRootException(Throwable cause) {
		super(ErrorCode.CATEGORY_DELETE_ROOT_CATEGORY, cause);
	}

	public CategoryCannotDeleteRootException(String message, Throwable cause) {
		super(ErrorCode.CATEGORY_DELETE_ROOT_CATEGORY, message, cause);
	}
}
