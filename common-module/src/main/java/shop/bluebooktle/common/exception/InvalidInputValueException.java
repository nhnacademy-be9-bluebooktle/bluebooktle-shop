package shop.bluebooktle.common.exception;

public class InvalidInputValueException extends ApplicationException {
	public InvalidInputValueException() {
		super(ErrorCode.INVALID_INPUT_VALUE);
	}

	public InvalidInputValueException(Throwable cause) {
		super(ErrorCode.INVALID_INPUT_VALUE, cause);
	}

	public InvalidInputValueException(String customMessage) {
		super(ErrorCode.INVALID_INPUT_VALUE, customMessage);
	}

	public InvalidInputValueException(String customMessage, Throwable cause) {
		super(ErrorCode.INVALID_INPUT_VALUE, customMessage, cause);
	}
}
