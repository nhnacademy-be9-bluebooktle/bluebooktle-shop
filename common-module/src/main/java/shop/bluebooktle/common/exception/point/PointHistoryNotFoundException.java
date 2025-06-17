package shop.bluebooktle.common.exception.point;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PointHistoryNotFoundException extends ApplicationException {
	public PointHistoryNotFoundException() {
		super(ErrorCode.POINT_HISTORY_NOT_FOUND);
	}

}
