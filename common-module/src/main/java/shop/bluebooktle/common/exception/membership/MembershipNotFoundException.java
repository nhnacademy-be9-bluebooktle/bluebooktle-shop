package shop.bluebooktle.common.exception.membership;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class MembershipNotFoundException extends ApplicationException {

	public MembershipNotFoundException(String message) {
		super(ErrorCode.USER_MEMBERSHIP_NOT_FOUNT, message);
	}

}
