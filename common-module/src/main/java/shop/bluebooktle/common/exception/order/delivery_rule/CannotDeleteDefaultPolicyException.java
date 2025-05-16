package shop.bluebooktle.common.exception.order.delivery_rule;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CannotDeleteDefaultPolicyException extends ApplicationException {
	public CannotDeleteDefaultPolicyException() {
		super(ErrorCode.DELIVERY_RULE_CANNOT_DELETE_DEFAULT);
	}

	public CannotDeleteDefaultPolicyException(String message) {
		super(ErrorCode.DELIVERY_RULE_CANNOT_DELETE_DEFAULT, message);
	}

	public CannotDeleteDefaultPolicyException(Throwable cause) {
		super(ErrorCode.DELIVERY_RULE_CANNOT_DELETE_DEFAULT, cause);
	}

	public CannotDeleteDefaultPolicyException(String message, Throwable cause) {
		super(ErrorCode.DELIVERY_RULE_CANNOT_DELETE_DEFAULT, message, cause);
	}
}
