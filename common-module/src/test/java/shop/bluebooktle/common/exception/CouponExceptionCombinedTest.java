package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponTypeNameAlreadyException;
import shop.bluebooktle.common.exception.coupon.CouponTypeNotFoundException;
import shop.bluebooktle.common.exception.coupon.FailedCouponNotFoundException;
import shop.bluebooktle.common.exception.coupon.InvalidCouponTargetException;
import shop.bluebooktle.common.exception.coupon.UserCouponAlreadyUsedException;
import shop.bluebooktle.common.exception.coupon.UserCouponNotFoundException;

class CouponExceptionCombinedTest {

	@Test
	@DisplayName("CouponNotFoundException 테스트")
	void couponNotFoundException() {
		CouponNotFoundException exception = new CouponNotFoundException();

		assertThat(exception)
			.isInstanceOf(CouponNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.K_COUPON_NOT_FOUND);
	}

	@Test
	@DisplayName("CouponTypeNameAlreadyException 테스트")
	void couponTypeNameAlreadyException() {
		CouponTypeNameAlreadyException exception = new CouponTypeNameAlreadyException();

		assertThat(exception)
			.isInstanceOf(CouponTypeNameAlreadyException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.K_COUPON_TYPE_NAME_ALREADY_EXISTS);
	}

	@Test
	@DisplayName("CouponTypeNotFoundException 테스트")
	void couponTypeNotFoundException() {
		CouponTypeNotFoundException exception = new CouponTypeNotFoundException();

		assertThat(exception)
			.isInstanceOf(CouponTypeNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.K_COUPON_TYPE_NOT_FOUND);
	}

	@Test
	@DisplayName("FailedCouponNotFoundException 테스트")
	void failedCouponNotFoundException() {
		FailedCouponNotFoundException exception = new FailedCouponNotFoundException();

		assertThat(exception)
			.isInstanceOf(FailedCouponNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.K_FAILED_COUPON_ISSUE_NOT_FOUND);
	}

	@Test
	@DisplayName("InvalidCouponTargetException 테스트")
	void invalidCouponTargetException() {
		String message = "잘못된 대상에게 쿠폰이 발급되었습니다.";
		InvalidCouponTargetException exception = new InvalidCouponTargetException(message);

		assertThat(exception)
			.isInstanceOf(InvalidCouponTargetException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.K_COUPON_INVALID_TARGET);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("UserCouponAlreadyUsedException 테스트")
	void userCouponAlreadyUsedException() {
		UserCouponAlreadyUsedException exception = new UserCouponAlreadyUsedException();

		assertThat(exception)
			.isInstanceOf(UserCouponAlreadyUsedException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.K_COUPON_ALREADY_USED);
	}

	@Test
	@DisplayName("UserCouponNotFoundException 테스트")
	void userCouponNotFoundException() {
		UserCouponNotFoundException exception = new UserCouponNotFoundException();

		assertThat(exception)
			.isInstanceOf(UserCouponNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.K_USER_COUPON_NOT_FOUND);
	}
}
