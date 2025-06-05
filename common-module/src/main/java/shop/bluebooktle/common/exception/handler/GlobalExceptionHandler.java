package shop.bluebooktle.common.exception.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApplicationException.class)
	protected ResponseEntity<JsendResponse<?>> handleApplicationException(ApplicationException ex) {
		ErrorCode errorCode = ex.getErrorCode();
		HttpStatus httpStatus = errorCode.getStatus();

		JsendResponse<?> response = JsendResponse.error(
			ex.getMessage(),
			errorCode.getCode()
		);

		log.error("ApplicationException | Code: {} | Message: {}", errorCode.getCode(), ex.getMessage(), ex);

		return new ResponseEntity<>(response, httpStatus);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<JsendResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
			.collect(Collectors.toMap(
				FieldError::getField,
				fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
				(existing, replacement) -> existing
			));

		log.warn("Validation Error: {}", fieldErrors, ex);

		return new ResponseEntity<>(JsendResponse.fail(fieldErrors), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BindException.class)
	protected ResponseEntity<JsendResponse<?>> handleBindException(BindException ex) {
		Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
			.collect(Collectors.toMap(
				FieldError::getField,
				fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Binding error",
				(existing, replacement) -> existing
			));

		log.warn("Bind Error: {}", fieldErrors, ex);

		return new ResponseEntity<>(JsendResponse.fail(fieldErrors), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<JsendResponse<?>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException ex) {
		String paramName = ex.getName();
		String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown";
		String message = String.format("Parameter '%s' should be of type %s", paramName, requiredType);

		Map<String, String> detail = new HashMap<>();
		detail.put(paramName, message);

		log.warn("Type Mismatch: {}", message, ex);

		return new ResponseEntity<>(JsendResponse.fail(detail), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<JsendResponse<?>> handleUnexpected(Exception ex) {
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		HttpStatus httpStatus = errorCode.getStatus();

		JsendResponse<?> response = JsendResponse.error(
			"예상치 못한 오류가 발생했습니다. 문제가 지속되면 관리자에게 문의하세요.",
			errorCode.getCode()
		);

		log.error("AuthUnexpectedServerError | Code: {} | Status: {}",
			errorCode.getCode(), httpStatus, ex);

		return new ResponseEntity<>(response, httpStatus);
	}

}
