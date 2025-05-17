package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AuthorAlreadyExistsException extends ApplicationException {
	public AuthorAlreadyExistsException(String name) {
		super(ErrorCode.AUTHOR_ALREADY_EXISTS,
			"Author already exists with name: " + name);
	}
}