package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class UserCouponAlreadyUsedException extends ApplicationException {
	public UserCouponAlreadyUsedException() {
		super(ErrorCode.K_COUPON_ALREADY_USED);
	}

	public UserCouponAlreadyUsedException(Throwable cause) {
		super(ErrorCode.K_COUPON_ALREADY_USED, cause);
	}

	public UserCouponAlreadyUsedException(String customMessage) {
		super(ErrorCode.K_COUPON_ALREADY_USED, customMessage);
	}

	public UserCouponAlreadyUsedException(String customMessage, Throwable cause) {
		super(ErrorCode.K_COUPON_ALREADY_USED, customMessage, cause);
	}
}
