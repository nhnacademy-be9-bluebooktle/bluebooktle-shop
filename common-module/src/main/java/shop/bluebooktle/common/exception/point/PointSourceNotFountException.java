package shop.bluebooktle.common.exception.point;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PointSourceNotFountException extends ApplicationException {
	public PointSourceNotFountException() {
		super(ErrorCode.POINT_SOURCE_TYPE_NOT_FOUND);
	}

}
