package shop.bluebooktle.common.exception.point;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PointPolicyNotFoundException extends ApplicationException {
	public PointPolicyNotFoundException() {
		super(ErrorCode.POINT_POLICY_NOT_FOUND);
	}

}
