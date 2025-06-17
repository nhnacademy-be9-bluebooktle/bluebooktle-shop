package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class UserCouponNotFoundException extends ApplicationException {
	public UserCouponNotFoundException() {
		super(ErrorCode.K_USER_COUPON_NOT_FOUND);
	}

}
