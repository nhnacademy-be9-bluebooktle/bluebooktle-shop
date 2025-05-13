package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookLikesAlreadyChecked extends ApplicationException {
	public BookLikesAlreadyChecked() {
		super(ErrorCode.BOOK_ALREADY_LIKED);
	}

	public BookLikesAlreadyChecked(String message) {
		super(ErrorCode.BOOK_ALREADY_LIKED, message);
	}

	public BookLikesAlreadyChecked(Throwable cause) {
		super(ErrorCode.BOOK_ALREADY_LIKED, cause);
	}

	public BookLikesAlreadyChecked(String message, Throwable cause) {
		super(ErrorCode.BOOK_ALREADY_LIKED, message, cause);
	}
}
