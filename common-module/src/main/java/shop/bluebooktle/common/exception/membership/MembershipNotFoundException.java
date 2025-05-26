package shop.bluebooktle.common.exception.membership;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

public class MembershipNotFoundException extends ApplicationException {
	public MembershipNotFoundException() {
		super(ErrorCode.USER_MEMBERSHIP_NOT_FOUNT);
	}

	public MembershipNotFoundException(String message) {
		super(ErrorCode.USER_MEMBERSHIP_NOT_FOUNT, message);
	}

	public MembershipNotFoundException(Throwable cause) {
		super(ErrorCode.USER_MEMBERSHIP_NOT_FOUNT, cause);
	}

	public MembershipNotFoundException(String message, Throwable cause) {
		super(ErrorCode.USER_MEMBERSHIP_NOT_FOUNT, message, cause);
	}
}
