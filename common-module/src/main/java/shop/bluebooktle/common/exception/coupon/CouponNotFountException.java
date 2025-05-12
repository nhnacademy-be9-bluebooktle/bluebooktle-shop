package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CouponNotFountException extends ApplicationException {
	public CouponNotFountException() {
		super(ErrorCode.K_COUPON_NOT_FOUND);
	}

	public CouponNotFountException(String message) {
		super(ErrorCode.K_COUPON_NOT_FOUND, message);
	}

	public CouponNotFountException(Throwable cause) {
		super(ErrorCode.K_COUPON_NOT_FOUND, cause);
	}

	public CouponNotFountException(String message, Throwable cause) {
		super(ErrorCode.K_COUPON_NOT_FOUND, message, cause);
	}
}
