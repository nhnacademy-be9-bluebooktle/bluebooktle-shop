package shop.bluebooktle.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

	private final ErrorCode errorCode;

	public ApplicationException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ApplicationException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public ApplicationException(ErrorCode errorCode, String customMessage) {
		super(customMessage);
		this.errorCode = errorCode;
	}

	public ApplicationException(ErrorCode errorCode, String customMessage, Throwable cause) {
		super(customMessage, cause);
		this.errorCode = errorCode;
	}

	public String getCode() {
		return errorCode.getCode();
	}

	public HttpStatus getHttpStatus() {
		return errorCode.getStatus();
	}
	
}