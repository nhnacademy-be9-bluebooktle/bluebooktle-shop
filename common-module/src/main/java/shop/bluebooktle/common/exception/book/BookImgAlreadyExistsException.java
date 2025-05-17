package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookImgAlreadyExistsException extends ApplicationException {
	public BookImgAlreadyExistsException(Long bookId, Long imgId) {
		super(ErrorCode.BOOK_IMG_ALREADY_EXISTS,
			String.format("Book(id=%d) and Img(id=%d) mapping already exists", bookId, imgId));
	}
}
