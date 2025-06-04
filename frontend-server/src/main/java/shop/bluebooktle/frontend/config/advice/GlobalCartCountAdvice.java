package shop.bluebooktle.frontend.config.advice;

import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.exception.cart.CartNotFoundException;
import shop.bluebooktle.frontend.service.CartService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalCartCountAdvice {

	private final CartService cartService;

	@ModelAttribute("cartItemCount")
	public int cartItemCount(HttpServletRequest request) {
		String guestId = null;

		if (WebUtils.getCookie(request, "GUEST_ID") != null) {
			guestId = WebUtils.getCookie(request, "GUEST_ID").getValue();
		}

		try {
			List<BookCartOrderResponse> cartItems = cartService.getCartItems(guestId); // backend에서 로그인 여부 판단
			return cartItems.size();
		} catch (CartNotFoundException e) {
			return 0;
		}
	}
}