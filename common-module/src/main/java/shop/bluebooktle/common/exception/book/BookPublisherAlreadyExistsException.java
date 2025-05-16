package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookPublisherAlreadyExistsException extends ApplicationException {
	public BookPublisherAlreadyExistsException() {
		super(ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS);
	}

	public BookPublisherAlreadyExistsException(String message) {
		super(ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS, message);
	}

	public BookPublisherAlreadyExistsException(Throwable cause) {
		super(ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS, cause);
	}

	public BookPublisherAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS, message, cause);
	}

	public BookPublisherAlreadyExistsException(Long bookId, Long publisherId) {
		super(
			ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS,
			String.format(
				"이미 등록된 도서-출판사 관계입니다. bookId=%d, publisherId=%d",
				bookId,
				publisherId
			)
		);
	}
}
