package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class ImgNotFoundException extends ApplicationException {
	public ImgNotFoundException() {
		super(ErrorCode.IMAGE_NOT_FOUND);
	}
}