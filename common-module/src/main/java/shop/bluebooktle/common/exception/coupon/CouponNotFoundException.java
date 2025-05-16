package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CouponNotFoundException extends ApplicationException {
	public CouponNotFoundException() {
		super(ErrorCode.K_COUPON_NOT_FOUND);
	}

	public CouponNotFoundException(String message) {
		super(ErrorCode.K_COUPON_NOT_FOUND, message);
	}

	public CouponNotFoundException(Throwable cause) {
		super(ErrorCode.K_COUPON_NOT_FOUND, cause);
	}

	public CouponNotFoundException(String message, Throwable cause) {
		super(ErrorCode.K_COUPON_NOT_FOUND, message, cause);
	}
}
