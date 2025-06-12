package shop.bluebooktle.frontend.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApplicationException.class)
	public String handleApplicationException(ApplicationException ex, RedirectAttributes redirectAttributes) {
		ErrorCode errorCode = ex.getErrorCode();

		redirectAttributes.addFlashAttribute("globalErrorTitle", errorCode.getCode());
		redirectAttributes.addFlashAttribute("globalErrorMessage", ex.getMessage());

		log.error("ApplicationException | Code: {} | Message: {}", errorCode.getCode(), ex.getMessage(), ex);

		return "redirect:/";
	}
}