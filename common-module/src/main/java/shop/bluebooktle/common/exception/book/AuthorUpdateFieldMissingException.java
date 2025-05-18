package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class AuthorUpdateFieldMissingException extends ApplicationException {
	public AuthorUpdateFieldMissingException() {
		super(ErrorCode.AUTHOR_UPDATE_FIELD_MISSING,
			"At least one field must be provided to update author");
	}
}
