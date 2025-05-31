package shop.bluebooktle.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.frontend.service.CartService;

@Controller
@RequiredArgsConstructor
public class CartConversionController {

	private final CartService cartService;

	@GetMapping("/merge")
	public String mergeOrConvert(@CookieValue(value = "GUEST_ID", required = false) String guestId) {
		cartService.mergeOrConvertGuestCart(guestId);
		return "redirect:/";
	}
}