package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CouponTypeNotFoundException extends ApplicationException {
	public CouponTypeNotFoundException() {
		super(ErrorCode.K_COUPON_TYPE_NOT_FOUND);
	}

	public CouponTypeNotFoundException(String message) {
		super(ErrorCode.K_COUPON_TYPE_NOT_FOUND, message);
	}

	public CouponTypeNotFoundException(Throwable cause) {
		super(ErrorCode.K_COUPON_TYPE_NOT_FOUND, cause);
	}

	public CouponTypeNotFoundException(String message, Throwable cause) {
		super(ErrorCode.K_COUPON_TYPE_NOT_FOUND, message, cause);
	}
}