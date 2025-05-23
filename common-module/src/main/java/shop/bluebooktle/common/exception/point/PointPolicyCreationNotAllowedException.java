package shop.bluebooktle.common.exception.point;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class PointPolicyCreationNotAllowedException extends ApplicationException {
	public PointPolicyCreationNotAllowedException() {
		super(ErrorCode.POINT_POLICY_CREATION_NOT_ALLOWED);
	}

	public PointPolicyCreationNotAllowedException(Throwable cause) {
		super(ErrorCode.POINT_POLICY_CREATION_NOT_ALLOWED, cause);
	}

	public PointPolicyCreationNotAllowedException(String customMessage) {
		super(ErrorCode.POINT_POLICY_CREATION_NOT_ALLOWED, customMessage);
	}

	public PointPolicyCreationNotAllowedException(String customMessage, Throwable cause) {
		super(ErrorCode.POINT_POLICY_CREATION_NOT_ALLOWED, customMessage, cause);
	}
}
