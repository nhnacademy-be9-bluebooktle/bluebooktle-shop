package shop.bluebooktle.auth.handler;

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

@RestControllerAdvice(basePackages = "shop.bluebooktle.auth.controller")
@Slf4j
public class AuthGlobalExceptionHandler {

	@ExceptionHandler(ApplicationException.class)
	protected ResponseEntity<JsendResponse<?>> handleApplicationException(ApplicationException ex) {
		ErrorCode errorCode = ex.getErrorCode();
		HttpStatus httpStatus = errorCode.getStatus();

		JsendResponse<?> response = JsendResponse.error(
			ex.getMessage() != null ? ex.getMessage() : errorCode.getMessage(),
			errorCode.getCode()
		);

		log.error("AuthApplicationException | Code: {} | Status: {} | Message: {}",
			errorCode.getCode(), httpStatus, ex.getMessage(), ex);

		return new ResponseEntity<>(response, httpStatus);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<JsendResponse<?>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
			.collect(Collectors.toMap(
				FieldError::getField,
				fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
				(existing, replacement) -> existing
			));

		JsendResponse<?> response = JsendResponse.fail(fieldErrors);

		log.warn("AuthValidationException | Errors: {}", fieldErrors, ex);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BindException.class)
	protected ResponseEntity<JsendResponse<?>> handleBindException(BindException ex) {
		Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
			.collect(Collectors.toMap(
				FieldError::getField,
				fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Binding error",
				(existing, replacement) -> existing
			));

		JsendResponse<?> response = JsendResponse.fail(fieldErrors);

		log.warn("AuthBindingException | Errors: {}", fieldErrors, ex);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<JsendResponse<?>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException ex) {
		String paramName = ex.getName();
		String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown";
		String message = String.format("Parameter '%s' should be of type %s", paramName, requiredType);

		Map<String, String> errorDetail = new HashMap<>();
		errorDetail.put(paramName, message);

		JsendResponse<?> response = JsendResponse.fail(errorDetail);

		log.warn("AuthMethodArgumentTypeMismatchException | Parameter: {} | Required Type: {}", paramName, requiredType,
			ex);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<JsendResponse<?>> handleGenericException(Exception ex) {
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		HttpStatus httpStatus = errorCode.getStatus();

		JsendResponse<?> response = JsendResponse.error(
			"인증/회원 서비스에서 예상치 못한 오류가 발생했습니다. 문제가 지속되면 관리자에게 문의하세요.",
			errorCode.getCode()
		);

		log.error("AuthUnexpectedServerError | Code: {} | Status: {}",
			errorCode.getCode(), httpStatus, ex);

		return new ResponseEntity<>(response, httpStatus);
	}
}