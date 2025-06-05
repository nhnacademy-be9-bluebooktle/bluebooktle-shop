package shop.bluebooktle.frontend.config.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.frontend.service.CartService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalCartCountAdvice {

	private final CartService cartService;

	@ModelAttribute("cartItemCount")
	public long cartItemCount(HttpServletRequest request) {
		String guestId = null;
		if (WebUtils.getCookie(request, "GUEST_ID") == null) {
			return 0L;
		}

		guestId = WebUtils.getCookie(request, "GUEST_ID").getValue();

		try {
			return cartService.getCartSize(guestId);
		} catch (Exception e) {

			return 0L;
		}
	}
}
