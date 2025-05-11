package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CategoryNotFoundException extends ApplicationException {
	public CategoryNotFoundException() {
		super(ErrorCode.CATEGORY_NOT_FOUND);
	}

	public CategoryNotFoundException(String message) {
		super(ErrorCode.CATEGORY_NOT_FOUND, message);
	}

	public CategoryNotFoundException(Throwable cause) {
		super(ErrorCode.CATEGORY_NOT_FOUND, cause);
	}

	public CategoryNotFoundException(String message, Throwable cause) {
		super(ErrorCode.CATEGORY_NOT_FOUND, message, cause);
	}

	public CategoryNotFoundException(Long id) {
		this(String.format("Category with id %d does not exist", id));
	}
}
