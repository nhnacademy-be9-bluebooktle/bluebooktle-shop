package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class InvalidCouponTargetException extends ApplicationException {
	public InvalidCouponTargetException() {
		super(ErrorCode.K_COUPON_INVALID_TARGET);
	}

	public InvalidCouponTargetException(String message) {
		super(ErrorCode.K_COUPON_INVALID_TARGET, message);
	}

	public InvalidCouponTargetException(Throwable cause) {
		super(ErrorCode.K_COUPON_INVALID_TARGET, cause);
	}

	public InvalidCouponTargetException(String message, Throwable cause) {
		super(ErrorCode.K_COUPON_INVALID_TARGET, message, cause);
	}

}