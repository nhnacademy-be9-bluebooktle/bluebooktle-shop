package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AuthorFieldNullException extends ApplicationException {
	public AuthorFieldNullException() {
		super(ErrorCode.AUTHOR_FIELD_NULL,
			"Author registration fields must not be null");
	}
}
