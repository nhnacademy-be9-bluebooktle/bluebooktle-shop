package shop.bluebooktle.common.exception.order.delivery_rule;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class DeliveryRuleAlreadyExistsException extends ApplicationException {
	public DeliveryRuleAlreadyExistsException() {
		super(ErrorCode.DELIVERY_RULE_ALREADY_EXISTS);
	}

	public DeliveryRuleAlreadyExistsException(String message) {
		super(ErrorCode.DELIVERY_RULE_ALREADY_EXISTS,
			ErrorCode.DELIVERY_RULE_ALREADY_EXISTS.getMessage() + " 정책명: " + message);
	}

	public DeliveryRuleAlreadyExistsException(Throwable cause) {
		super(ErrorCode.DELIVERY_RULE_ALREADY_EXISTS, cause);
	}

	public DeliveryRuleAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.DELIVERY_RULE_ALREADY_EXISTS, message, cause);
	}
}
