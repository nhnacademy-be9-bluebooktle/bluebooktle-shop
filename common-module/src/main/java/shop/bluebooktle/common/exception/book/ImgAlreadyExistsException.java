package shop.bluebooktle.common.exception.book;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class ImgAlreadyExistsException extends ApplicationException {
	public ImgAlreadyExistsException(String url) {
		super(ErrorCode.IMAGE_ALREADY_EXISTS,
			"Image with URL '" + url + "' already exists");
	}
}