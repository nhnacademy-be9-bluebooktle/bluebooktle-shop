package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class CouponTypeNameAlreadyException extends ApplicationException {

	public CouponTypeNameAlreadyException() {
		super(ErrorCode.K_COUPON_TYPE_NAME_ALREADY_EXISTS);
	}

}