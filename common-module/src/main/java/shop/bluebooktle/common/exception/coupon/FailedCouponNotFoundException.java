package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class FailedCouponNotFoundException extends ApplicationException {
	public FailedCouponNotFoundException() {
		super(ErrorCode.K_FAILED_COUPON_ISSUE_NOT_FOUND);
	}

	public FailedCouponNotFoundException(String message) {
		super(ErrorCode.K_FAILED_COUPON_ISSUE_NOT_FOUND, message);
	}

	public FailedCouponNotFoundException(Throwable cause) {
		super(ErrorCode.K_FAILED_COUPON_ISSUE_NOT_FOUND, cause);
	}

	public FailedCouponNotFoundException(String message, Throwable cause) {
		super(ErrorCode.K_FAILED_COUPON_ISSUE_NOT_FOUND, message, cause);
	}
}