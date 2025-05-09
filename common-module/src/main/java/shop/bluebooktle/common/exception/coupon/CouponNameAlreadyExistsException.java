package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CouponNameAlreadyExistsException extends ApplicationException {

	public CouponNameAlreadyExistsException() {
		super(ErrorCode.K_COUPON_NAME_ALREADY_EXISTS);
	}

	public CouponNameAlreadyExistsException(String message) {
		super(ErrorCode.K_COUPON_NAME_ALREADY_EXISTS, message);
	}

	public CouponNameAlreadyExistsException(Throwable cause) {
		super(ErrorCode.K_COUPON_NAME_ALREADY_EXISTS, cause);
	}

	public CouponNameAlreadyExistsException(String message, Throwable cause) {
		super(ErrorCode.K_COUPON_NAME_ALREADY_EXISTS, message, cause);
	}
}