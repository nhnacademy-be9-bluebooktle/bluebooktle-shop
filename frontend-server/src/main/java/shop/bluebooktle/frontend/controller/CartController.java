package shop.bluebooktle.frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.cart.response.CartItemResponse;
import shop.bluebooktle.frontend.service.CartService;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	// === 장바구니 담기 ===
	@PostMapping
	public String addToCart(@RequestParam Long bookId,
		@RequestParam int quantity,
		@RequestHeader(name = "Authorization", required = false) String token,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response) {
		if (isLoggedIn(token)) {
			cartService.addToMemberCart(bookId, quantity);
		} else {
			validateGuestId(guestId, response);
			cartService.addToGuestCart(guestId, bookId, quantity);
		}
		return "redirect:/cart";
	}

	// === 장바구니 목록 조회 ===
	@GetMapping
	public String getCartItems(@RequestHeader(name = "Authorization", required = false) String token,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response,
		Model model) {
		List<CartItemResponse> cartItems;
		if (isLoggedIn(token)) {
			cartItems = cartService.getMemberCartItems();
		} else {
			validateGuestId(guestId, response);
			cartItems = cartService.getGuestCartItems(guestId);
		}
		model.addAttribute("cartItems", cartItems);
		return "cart/view";
	}

	// === 수량 증가 ===
	@PatchMapping("/increase")
	public String increaseQuantity(@RequestParam Long bookId,
		@RequestHeader(name = "Authorization", required = false) String token,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response) {
		if (isLoggedIn(token)) {
			cartService.increaseMemberQuantity(bookId);
		} else {
			validateGuestId(guestId, response);
			cartService.increaseGuestQuantity(guestId, bookId);
		}
		return "redirect:/cart";
	}

	// === 수량 감소 ===
	@PatchMapping("/decrease")
	public String decreaseQuantity(@RequestParam Long bookId,
		@RequestHeader(name = "Authorization", required = false) String token,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response) {
		if (isLoggedIn(token)) {
			cartService.decreaseMemberQuantity(bookId);
		} else {
			validateGuestId(guestId, response);
			cartService.decreaseGuestQuantity(guestId, bookId);
		}
		return "redirect:/cart";
	}

	// === 단일 삭제 ===
	@DeleteMapping
	public String removeOne(@RequestParam Long bookId,
		@RequestHeader(name = "Authorization", required = false) String token,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response) {
		if (isLoggedIn(token)) {
			cartService.removeOneFromMemberCart(bookId);
		} else {
			validateGuestId(guestId, response);
			cartService.removeOneFromGuestCart(guestId, bookId);
		}
		return "redirect:/cart";
	}

	// === 선택 삭제 ===
	@DeleteMapping("/selected")
	public String removeSelected(@RequestParam("bookIds") List<Long> bookIds,
		@RequestHeader(name = "Authorization", required = false) String token,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response) {
		if (isLoggedIn(token)) {
			cartService.removeSelectedFromMemberCart(bookIds);
		} else {
			validateGuestId(guestId, response);
			cartService.removeSelectedFromGuestCart(guestId, bookIds);
		}
		return "redirect:/cart";
	}

	// === 보조 메서드 ===
	private boolean isLoggedIn(String token) {
		return token != null && !token.isBlank();
	}

	private void validateGuestId(String guestId, HttpServletResponse response) {
		if (guestId == null || guestId.isBlank()) {
			throw new IllegalStateException("guestId가 존재하지 않습니다. 쿠키를 발급받아야 합니다.");
		}
	}
}
