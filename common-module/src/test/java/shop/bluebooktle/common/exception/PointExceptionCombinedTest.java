package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.point.PointHistoryNotFoundException;
import shop.bluebooktle.common.exception.point.PointInSufficeientBalance;
import shop.bluebooktle.common.exception.point.PointPolicyCreationNotAllowedException;
import shop.bluebooktle.common.exception.point.PointPolicyNotFoundException;
import shop.bluebooktle.common.exception.point.PointSourceNotFountException;

class PointExceptionCombinedTest {

	@Test
	@DisplayName("PointHistoryNotFoundException 테스트")
	void pointHistoryNotFoundException() {
		PointHistoryNotFoundException exception = new PointHistoryNotFoundException();

		assertThat(exception).isInstanceOf(ApplicationException.class);
		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_HISTORY_NOT_FOUND);
	}

	@Test
	@DisplayName("PointPolicyNotFoundException 테스트")
	void pointPolicyNotFoundException() {
		PointPolicyNotFoundException exception = new PointPolicyNotFoundException();

		assertThat(exception).isInstanceOf(ApplicationException.class);
		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_POLICY_NOT_FOUND);
	}

	@Test
	@DisplayName("PointSourceNotFountException 테스트")
	void pointSourceNotFountException() {
		PointSourceNotFountException exception = new PointSourceNotFountException();

		assertThat(exception).isInstanceOf(ApplicationException.class);
		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_SOURCE_TYPE_NOT_FOUND);
	}

	@Nested
	class PointInSufficeientBalanceTest {

		@Test
		@DisplayName("기본 생성자")
		void defaultConstructor() {
			PointInSufficeientBalance exception = new PointInSufficeientBalance();

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_INSUFFICIENT_BALANCE);
		}

		@Test
		@DisplayName("custom message 생성자")
		void messageConstructor() {
			String msg = "사용 가능한 포인트 부족";
			PointInSufficeientBalance exception = new PointInSufficeientBalance(msg);

			assertThat(exception.getMessage()).contains(msg);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_INSUFFICIENT_BALANCE);
		}

		@Test
		@DisplayName("cause 생성자")
		void causeConstructor() {
			Throwable cause = new RuntimeException("DB 문제");
			PointInSufficeientBalance exception = new PointInSufficeientBalance(cause);

			assertThat(exception.getCause()).isEqualTo(cause);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_INSUFFICIENT_BALANCE);
		}

		@Test
		@DisplayName("custom message + cause 생성자")
		void messageAndCauseConstructor() {
			String msg = "포인트 부족 오류";
			Throwable cause = new IllegalStateException("포인트 테이블 손상");
			PointInSufficeientBalance exception = new PointInSufficeientBalance(msg, cause);

			assertThat(exception.getMessage()).contains(msg);
			assertThat(exception.getCause()).isEqualTo(cause);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_INSUFFICIENT_BALANCE);
		}
	}

	@Nested
	class PointPolicyCreationNotAllowedTest {

		@Test
		@DisplayName("기본 생성자")
		void defaultConstructor() {
			PointPolicyCreationNotAllowedException exception = new PointPolicyCreationNotAllowedException();

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_POLICY_CREATION_NOT_ALLOWED);
		}

		@Test
		@DisplayName("custom message 생성자")
		void messageConstructor() {
			String msg = "정책 생성 불가";
			PointPolicyCreationNotAllowedException exception = new PointPolicyCreationNotAllowedException(msg);

			assertThat(exception.getMessage()).contains(msg);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_POLICY_CREATION_NOT_ALLOWED);
		}

		@Test
		@DisplayName("cause 생성자")
		void causeConstructor() {
			Throwable cause = new IllegalArgumentException("기존 정책 충돌");
			PointPolicyCreationNotAllowedException exception = new PointPolicyCreationNotAllowedException(cause);

			assertThat(exception.getCause()).isEqualTo(cause);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_POLICY_CREATION_NOT_ALLOWED);
		}

		@Test
		@DisplayName("custom message + cause 생성자")
		void messageAndCauseConstructor() {
			String msg = "정책 중복";
			Throwable cause = new RuntimeException("중복된 정책 이름");
			PointPolicyCreationNotAllowedException exception = new PointPolicyCreationNotAllowedException(msg, cause);

			assertThat(exception.getMessage()).contains(msg);
			assertThat(exception.getCause()).isEqualTo(cause);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_POLICY_CREATION_NOT_ALLOWED);
		}
	}

}
