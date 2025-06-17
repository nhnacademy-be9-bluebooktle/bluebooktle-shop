package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class UserCouponAlreadyUsedException extends ApplicationException {
	public UserCouponAlreadyUsedException() {
		super(ErrorCode.K_COUPON_ALREADY_USED);
	}

}
