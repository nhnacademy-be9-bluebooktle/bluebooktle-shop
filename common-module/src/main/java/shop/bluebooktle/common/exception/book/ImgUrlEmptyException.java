package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class ImgUrlEmptyException extends ApplicationException {
	public ImgUrlEmptyException() {
		super(ErrorCode.IMAGE_URL_EMPTY);
	}
	public ImgUrlEmptyException(String message) {
		super(ErrorCode.IMAGE_URL_EMPTY, message);
	}
}