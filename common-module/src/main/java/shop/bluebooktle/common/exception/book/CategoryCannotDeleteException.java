package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CategoryCannotDeleteException extends ApplicationException {

	public CategoryCannotDeleteException() {
		super(ErrorCode.CATEGORY_REQUIRED);
	}

	public CategoryCannotDeleteException(String message) {
		super(ErrorCode.CATEGORY_REQUIRED, message);
	}

	public CategoryCannotDeleteException(Throwable cause) {
		super(ErrorCode.CATEGORY_REQUIRED, cause);
	}

	public CategoryCannotDeleteException(String message, Throwable cause) {
		super(ErrorCode.CATEGORY_REQUIRED, message, cause);
	}
}
