package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookTagAlreadyExistsException extends ApplicationException {
	public BookTagAlreadyExistsException() {
		super(ErrorCode.BOOK_TAG_ALREADY_EXISTS);
	}

	public BookTagAlreadyExistsException(String message) {
		super(ErrorCode.BOOK_TAG_ALREADY_EXISTS, message);
	}

	public BookTagAlreadyExistsException(Throwable cause) {
		super(ErrorCode.BOOK_TAG_ALREADY_EXISTS, cause);
	}

	public BookTagAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.BOOK_TAG_ALREADY_EXISTS, message, cause);
	}

	public BookTagAlreadyExistsException(Long bookId, Long tagId) {
		super(
			ErrorCode.BOOK_TAG_ALREADY_EXISTS,
			String.format(
				"이미 등록된 도서-태그 관계입니다. bookId=%d, tagId=%d",
				bookId,
				tagId
			)
		);
	}
}
