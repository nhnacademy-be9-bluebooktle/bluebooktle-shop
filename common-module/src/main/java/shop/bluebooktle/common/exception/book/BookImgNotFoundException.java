package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class BookImgNotFoundException extends ApplicationException {
	public BookImgNotFoundException(Long bookId, Long imgId) {
		super(ErrorCode.BOOK_IMG_NOT_FOUND,
			String.format("Book(id=%d) and Img(id=%d) mapping not found", bookId, imgId));
	}
}
