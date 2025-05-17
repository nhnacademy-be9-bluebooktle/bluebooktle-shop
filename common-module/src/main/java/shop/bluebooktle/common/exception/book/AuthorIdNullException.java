package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AuthorIdNullException extends ApplicationException {
	public AuthorIdNullException() {
		super(ErrorCode.AUTHOR_ID_NULL,
			"Author ID must not be null");
	}
}
