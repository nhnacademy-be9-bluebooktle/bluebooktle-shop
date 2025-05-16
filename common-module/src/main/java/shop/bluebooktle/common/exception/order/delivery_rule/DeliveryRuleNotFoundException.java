package shop.bluebooktle.common.exception.order.delivery_rule;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class DeliveryRuleNotFoundException extends ApplicationException {
	public DeliveryRuleNotFoundException() {
		super(ErrorCode.DELIVERY_RULE_NOT_FOUND);
	}

	public DeliveryRuleNotFoundException(String message) {
		super(ErrorCode.DELIVERY_RULE_NOT_FOUND, message);
	}

	public DeliveryRuleNotFoundException(Throwable cause) {
		super(ErrorCode.DELIVERY_RULE_NOT_FOUND, cause);
	}

	public DeliveryRuleNotFoundException(String message, Throwable cause) {
		super(ErrorCode.DELIVERY_RULE_NOT_FOUND, message, cause);
	}
}
