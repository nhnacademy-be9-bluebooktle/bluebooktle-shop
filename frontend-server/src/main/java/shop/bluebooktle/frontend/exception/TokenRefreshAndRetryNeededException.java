package shop.bluebooktle.frontend.exception;

public class TokenRefreshAndRetryNeededException extends RuntimeException {
	public TokenRefreshAndRetryNeededException(String message) {
		super(message);
	}

	public TokenRefreshAndRetryNeededException(String message, Throwable cause) {
		super(message, cause);
	}
}