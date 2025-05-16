package shop.bluebooktle.common.exception.order.delivery_rule;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class DefaultDeliveryRuleNotFoundException extends ApplicationException {
	public DefaultDeliveryRuleNotFoundException() {
		super(ErrorCode.DEFAULT_DELIVERY_RULE_NOT_FOUND);
	}

	public DefaultDeliveryRuleNotFoundException(String message) {
		super(ErrorCode.DEFAULT_DELIVERY_RULE_NOT_FOUND, message);
	}

	public DefaultDeliveryRuleNotFoundException(Throwable cause) {
		super(ErrorCode.DEFAULT_DELIVERY_RULE_NOT_FOUND, cause);
	}

	public DefaultDeliveryRuleNotFoundException(String message, Throwable cause) {
		super(ErrorCode.DEFAULT_DELIVERY_RULE_NOT_FOUND, message, cause);
	}
}
