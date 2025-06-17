package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class InvalidCouponTargetException extends ApplicationException {

	public InvalidCouponTargetException(String message) {
		super(ErrorCode.K_COUPON_INVALID_TARGET, message);
	}

}