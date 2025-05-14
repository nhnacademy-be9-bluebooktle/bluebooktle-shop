package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookPublisherNotFoundException extends ApplicationException {
	public BookPublisherNotFoundException() {
		super(ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS);
	}

	public BookPublisherNotFoundException(String message) {
		super(ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS, message);
	}

	public BookPublisherNotFoundException(Throwable cause) {
		super(ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS, cause);
	}

	public BookPublisherNotFoundException(String message, Throwable cause) {
		super(ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS, message, cause);
	}

	public BookPublisherNotFoundException(Long bookId, Long publisherId) {
		super(
			ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS,
			String.format(
				"도서-출판사 관계를 찾을 수 없습니다. bookId=%d, publisherId=%d",
				bookId,
				publisherId
			)
		);
	}
}
