package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AuthorNotFoundException extends ApplicationException {
	public AuthorNotFoundException() {
		super(ErrorCode.AUTHOR_NOT_FOUND);
	}

	public AuthorNotFoundException(Long id) {
		super(ErrorCode.AUTHOR_NOT_FOUND,
			"Author not found with ID: " + id);
	}

}
