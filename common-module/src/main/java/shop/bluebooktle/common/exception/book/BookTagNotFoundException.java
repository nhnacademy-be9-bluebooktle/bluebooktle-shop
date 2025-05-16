package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookTagNotFoundException extends ApplicationException {
	public BookTagNotFoundException() {
		super(ErrorCode.BOOK_TAG_NOT_FOUND);
	}

	public BookTagNotFoundException(String message) {
		super(ErrorCode.BOOK_TAG_NOT_FOUND, message);
	}

	public BookTagNotFoundException(Throwable cause) {
		super(ErrorCode.BOOK_TAG_NOT_FOUND, cause);
	}

	public BookTagNotFoundException(String message, Throwable cause) {
		super(ErrorCode.BOOK_TAG_NOT_FOUND, message, cause);
	}

	public BookTagNotFoundException(Long bookId, Long publisherId) {
		super(
			ErrorCode.BOOK_TAG_NOT_FOUND,
			String.format(
				"도서-태그 관계를 찾을 수 없습니다. bookId=%d, publisherId=%d",
				bookId,
				publisherId
			)
		);
	}
}
