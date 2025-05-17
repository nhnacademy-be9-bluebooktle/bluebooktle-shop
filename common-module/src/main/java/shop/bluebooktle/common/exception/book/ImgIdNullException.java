package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class ImgIdNullException extends ApplicationException {
	public ImgIdNullException() {
		super(ErrorCode.IMAGE_ID_NULL);
	}
	public ImgIdNullException(String message) {
		super(ErrorCode.IMAGE_ID_NULL, message);
	}
}