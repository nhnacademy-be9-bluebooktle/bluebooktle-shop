package shop.bluebooktle.common.exception.order.delivery_rule;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class DefaultDeliveryRuleNotFoundException extends ApplicationException {
	public DefaultDeliveryRuleNotFoundException() {
		super(ErrorCode.DEFAULT_DELIVERY_RULE_NOT_FOUND);
	}

}
