package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CouponTypeNameAlreadyException extends ApplicationException {

	public CouponTypeNameAlreadyException() {
		super(ErrorCode.K_COUPON_TYPE_NAME_ALREADY_EXISTS);
	}

	public CouponTypeNameAlreadyException(String message) {
		super(ErrorCode.K_COUPON_TYPE_NAME_ALREADY_EXISTS, message);
	}

	public CouponTypeNameAlreadyException(Throwable cause) {
		super(ErrorCode.K_COUPON_TYPE_NAME_ALREADY_EXISTS, cause);
	}

	public CouponTypeNameAlreadyException(String message, Throwable cause) {
		super(ErrorCode.K_COUPON_TYPE_NAME_ALREADY_EXISTS, message, cause);
	}
}