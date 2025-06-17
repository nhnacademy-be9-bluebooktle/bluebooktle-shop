package shop.bluebooktle.common.exception.coupon;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class FailedCouponNotFoundException extends ApplicationException {
	public FailedCouponNotFoundException() {
		super(ErrorCode.K_FAILED_COUPON_ISSUE_NOT_FOUND);
	}

}