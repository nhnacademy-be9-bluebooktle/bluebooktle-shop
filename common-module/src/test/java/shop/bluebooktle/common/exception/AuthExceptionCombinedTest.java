package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.auth.AuthenticationFailedException;
import shop.bluebooktle.common.exception.auth.DormantAccountException;
import shop.bluebooktle.common.exception.auth.EmailAlreadyExistsException;
import shop.bluebooktle.common.exception.auth.HandleAccessDeniedException;
import shop.bluebooktle.common.exception.auth.InvalidRefreshTokenException;
import shop.bluebooktle.common.exception.auth.InvalidTokenException;
import shop.bluebooktle.common.exception.auth.LoginIdAlreadyExistsException;
import shop.bluebooktle.common.exception.auth.UnauthoriedException;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.auth.WithdrawnAccountException;

class AuthExceptionCombinedTest {

	@Test
	@DisplayName("AuthenticationFailedException 기본 생성자")
	void testAuthenticationFailedException_defaultConstructor() {
		AuthenticationFailedException exception = new AuthenticationFailedException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_AUTHENTICATION_FAILED);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.AUTH_AUTHENTICATION_FAILED.getMessage());
	}

	@Test
	@DisplayName("AuthenticationFailedException 메시지 생성자")
	void testAuthenticationFailedException_messageConstructor() {
		String message = "Custom message";
		AuthenticationFailedException exception = new AuthenticationFailedException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_AUTHENTICATION_FAILED);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("AuthenticationFailedException cause 생성자")
	void testAuthenticationFailedException_causeConstructor() {
		Throwable cause = new RuntimeException("Cause");
		AuthenticationFailedException exception = new AuthenticationFailedException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_AUTHENTICATION_FAILED);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("AuthenticationFailedException 메시지 + cause 생성자")
	void testAuthenticationFailedException_messageAndCauseConstructor() {
		String message = "With cause";
		Throwable cause = new RuntimeException("Cause");
		AuthenticationFailedException exception = new AuthenticationFailedException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_AUTHENTICATION_FAILED);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	// ---------------- DormantAccountException ----------------

	@Test
	@DisplayName("DormantAccountException 기본 생성자")
	void testDormantAccountException_defaultConstructor() {
		DormantAccountException exception = new DormantAccountException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INACTIVE_ACCOUNT);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.AUTH_INACTIVE_ACCOUNT.getMessage());
	}

	@Test
	@DisplayName("DormantAccountException 메시지 생성자")
	void testDormantAccountException_messageConstructor() {
		String message = "Account is dormant";
		DormantAccountException exception = new DormantAccountException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INACTIVE_ACCOUNT);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("DormantAccountException cause 생성자")
	void testDormantAccountException_causeConstructor() {
		Throwable cause = new RuntimeException("Dormant cause");
		DormantAccountException exception = new DormantAccountException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INACTIVE_ACCOUNT);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("DormantAccountException 메시지 + cause 생성자")
	void testDormantAccountException_messageAndCauseConstructor() {
		String message = "Dormant with cause";
		Throwable cause = new RuntimeException("Dormant cause");
		DormantAccountException exception = new DormantAccountException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INACTIVE_ACCOUNT);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	// ---------------- EmailAlreadyExistsException ----------------

	@Test
	@DisplayName("EmailAlreadyExistsException 기본 생성자")
	void testEmailAlreadyExistsException_defaultConstructor() {
		EmailAlreadyExistsException exception = new EmailAlreadyExistsException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS.getMessage());
	}

	@Test
	@DisplayName("EmailAlreadyExistsException 메시지 생성자")
	void testEmailAlreadyExistsException_messageConstructor() {
		String message = "Email exists";
		EmailAlreadyExistsException exception = new EmailAlreadyExistsException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("EmailAlreadyExistsException cause 생성자")
	void testEmailAlreadyExistsException_causeConstructor() {
		Throwable cause = new RuntimeException("Duplicate email");
		EmailAlreadyExistsException exception = new EmailAlreadyExistsException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("EmailAlreadyExistsException 메시지 + cause 생성자")
	void testEmailAlreadyExistsException_messageAndCauseConstructor() {
		String message = "Email already exists with cause";
		Throwable cause = new RuntimeException("Duplicate");
		EmailAlreadyExistsException exception = new EmailAlreadyExistsException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	// ---------------- HandleAccessDeniedException ----------------

	@Test
	@DisplayName("HandleAccessDeniedException 기본 생성자")
	void testHandleAccessDeniedException_defaultConstructor() {
		HandleAccessDeniedException exception = new HandleAccessDeniedException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HANDLE_ACCESS_DENIED);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.HANDLE_ACCESS_DENIED.getMessage());
	}

	@Test
	@DisplayName("HandleAccessDeniedException 메시지 생성자")
	void testHandleAccessDeniedException_messageConstructor() {
		String message = "Access denied";
		HandleAccessDeniedException exception = new HandleAccessDeniedException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HANDLE_ACCESS_DENIED);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("HandleAccessDeniedException cause 생성자")
	void testHandleAccessDeniedException_causeConstructor() {
		Throwable cause = new RuntimeException("Security block");
		HandleAccessDeniedException exception = new HandleAccessDeniedException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HANDLE_ACCESS_DENIED);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("HandleAccessDeniedException 메시지 + cause 생성자")
	void testHandleAccessDeniedException_messageAndCauseConstructor() {
		String message = "Denied with reason";
		Throwable cause = new RuntimeException("Access issue");
		HandleAccessDeniedException exception = new HandleAccessDeniedException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HANDLE_ACCESS_DENIED);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("InvalidRefreshTokenException 기본 생성자")
	void testInvalidRefreshTokenException_defaultConstructor() {
		InvalidRefreshTokenException exception = new InvalidRefreshTokenException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.AUTH_INVALID_REFRESH_TOKEN.getMessage());
	}

	@Test
	@DisplayName("InvalidRefreshTokenException 메시지 생성자")
	void testInvalidRefreshTokenException_messageConstructor() {
		String message = "Invalid refresh";
		InvalidRefreshTokenException exception = new InvalidRefreshTokenException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("InvalidRefreshTokenException cause 생성자")
	void testInvalidRefreshTokenException_causeConstructor() {
		Throwable cause = new RuntimeException("Invalid refresh cause");
		InvalidRefreshTokenException exception = new InvalidRefreshTokenException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("InvalidRefreshTokenException 메시지 + cause 생성자")
	void testInvalidRefreshTokenException_messageAndCauseConstructor() {
		String message = "Invalid refresh with cause";
		Throwable cause = new RuntimeException("Cause");
		InvalidRefreshTokenException exception = new InvalidRefreshTokenException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("InvalidTokenException 기본 생성자")
	void testInvalidTokenException_defaultConstructor() {
		InvalidTokenException exception = new InvalidTokenException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_TOKEN_VALIDATION_FAILED);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.AUTH_TOKEN_VALIDATION_FAILED.getMessage());
	}

	@Test
	@DisplayName("InvalidTokenException 메시지 생성자")
	void testInvalidTokenException_messageConstructor() {
		String message = "Token invalid";
		InvalidTokenException exception = new InvalidTokenException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_TOKEN_VALIDATION_FAILED);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("InvalidTokenException cause 생성자")
	void testInvalidTokenException_causeConstructor() {
		Throwable cause = new RuntimeException("Token validation failed");
		InvalidTokenException exception = new InvalidTokenException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_TOKEN_VALIDATION_FAILED);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("InvalidTokenException 메시지 + cause 생성자")
	void testInvalidTokenException_messageAndCauseConstructor() {
		String message = "Token failed";
		Throwable cause = new RuntimeException("Invalid");
		InvalidTokenException exception = new InvalidTokenException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_TOKEN_VALIDATION_FAILED);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("LoginIdAlreadyExistsException 기본 생성자")
	void testLoginIdAlreadyExistsException_defaultConstructor() {
		LoginIdAlreadyExistsException exception = new LoginIdAlreadyExistsException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_LOGIN_ID_ALREADY_EXISTS);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.AUTH_LOGIN_ID_ALREADY_EXISTS.getMessage());
	}

	@Test
	@DisplayName("LoginIdAlreadyExistsException 메시지 생성자")
	void testLoginIdAlreadyExistsException_messageConstructor() {
		String message = "Login ID exists";
		LoginIdAlreadyExistsException exception = new LoginIdAlreadyExistsException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_LOGIN_ID_ALREADY_EXISTS);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("LoginIdAlreadyExistsException cause 생성자")
	void testLoginIdAlreadyExistsException_causeConstructor() {
		Throwable cause = new RuntimeException("Login ID conflict");
		LoginIdAlreadyExistsException exception = new LoginIdAlreadyExistsException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_LOGIN_ID_ALREADY_EXISTS);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("LoginIdAlreadyExistsException 메시지 + cause 생성자")
	void testLoginIdAlreadyExistsException_messageAndCauseConstructor() {
		String message = "Login conflict";
		Throwable cause = new RuntimeException("Cause");
		LoginIdAlreadyExistsException exception = new LoginIdAlreadyExistsException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_LOGIN_ID_ALREADY_EXISTS);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("UnauthoriedException 기본 생성자")
	void testUnauthoriedException_defaultConstructor() {
		UnauthoriedException exception = new UnauthoriedException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.UNAUTHORIZED.getMessage());
	}

	@Test
	@DisplayName("UnauthoriedException 메시지 생성자")
	void testUnauthoriedException_messageConstructor() {
		String message = "Unauthorized access";
		UnauthoriedException exception = new UnauthoriedException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("UnauthoriedException cause 생성자")
	void testUnauthoriedException_causeConstructor() {
		Throwable cause = new RuntimeException("No auth token");
		UnauthoriedException exception = new UnauthoriedException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("UnauthoriedException 메시지 + cause 생성자")
	void testUnauthoriedException_messageAndCauseConstructor() {
		String message = "Unauthorized and failed";
		Throwable cause = new RuntimeException("No access");
		UnauthoriedException exception = new UnauthoriedException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("UserNotFoundException 기본 생성자")
	void testUserNotFoundException_defaultConstructor() {
		UserNotFoundException exception = new UserNotFoundException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_USER_NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.AUTH_USER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("UserNotFoundException 메시지 생성자")
	void testUserNotFoundException_messageConstructor() {
		String message = "User is missing";
		UserNotFoundException exception = new UserNotFoundException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_USER_NOT_FOUND);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("UserNotFoundException cause 생성자")
	void testUserNotFoundException_causeConstructor() {
		Throwable cause = new RuntimeException("No such user");
		UserNotFoundException exception = new UserNotFoundException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_USER_NOT_FOUND);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("UserNotFoundException 메시지 + cause 생성자")
	void testUserNotFoundException_messageAndCauseConstructor() {
		String message = "User error";
		Throwable cause = new RuntimeException("User cause");
		UserNotFoundException exception = new UserNotFoundException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_USER_NOT_FOUND);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("WithdrawnAccountException 기본 생성자")
	void testWithdrawnAccountException_defaultConstructor() {
		WithdrawnAccountException exception = new WithdrawnAccountException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_ACCOUNT_WITHDRAWN);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.AUTH_ACCOUNT_WITHDRAWN.getMessage());
	}

	@Test
	@DisplayName("WithdrawnAccountException 메시지 생성자")
	void testWithdrawnAccountException_messageConstructor() {
		String message = "Account is withdrawn";
		WithdrawnAccountException exception = new WithdrawnAccountException(message);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_ACCOUNT_WITHDRAWN);
		assertThat(exception.getMessage()).contains(message);
	}

	@Test
	@DisplayName("WithdrawnAccountException cause 생성자")
	void testWithdrawnAccountException_causeConstructor() {
		Throwable cause = new RuntimeException("Withdrawn cause");
		WithdrawnAccountException exception = new WithdrawnAccountException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_ACCOUNT_WITHDRAWN);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("WithdrawnAccountException 메시지 + cause 생성자")
	void testWithdrawnAccountException_messageAndCauseConstructor() {
		String message = "Withdrawn with cause";
		Throwable cause = new RuntimeException("Cause");
		WithdrawnAccountException exception = new WithdrawnAccountException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_ACCOUNT_WITHDRAWN);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

}

