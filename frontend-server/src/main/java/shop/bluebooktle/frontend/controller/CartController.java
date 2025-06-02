package shop.bluebooktle.frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.exception.cart.GuestUserNotFoundException;
import shop.bluebooktle.frontend.service.CartService;

@Slf4j
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	// === 장바구니 담기 ===
	@PostMapping
	public String addToCart(@RequestParam Long bookId,
		@RequestParam int quantity,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response,
		RedirectAttributes redirectAttributes) {

		validateGuestId(guestId, response);
		cartService.addToCart(guestId, bookId, quantity);
		redirectAttributes.addFlashAttribute("showCartSuccess", true);
		return "redirect:/cart";
	}

	// === 장바구니 목록 조회 ===
	@GetMapping
	public String getCartItems(@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response,
		Model model) {

		validateGuestId(guestId, response);
		List<BookCartOrderResponse> cartItems = cartService.getCartItems(guestId);
		model.addAttribute("cartItems", cartItems);
		return "cart/cart";
	}

	// === 수량 증가 ===
	@PostMapping("/increase")
	public String increaseQuantity(@RequestParam Long bookId,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response) {

		validateGuestId(guestId, response);
		cartService.increaseQuantity(guestId, bookId);
		return "redirect:/cart";
	}

	// === 수량 감소 ===
	@PostMapping("/decrease")
	public String decreaseQuantity(@RequestParam Long bookId,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response) {

		validateGuestId(guestId, response);
		cartService.decreaseQuantity(guestId, bookId);
		return "redirect:/cart";
	}

	// === 단일 삭제 ===
	@DeleteMapping
	public String removeOne(@RequestParam Long bookId,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response) {

		validateGuestId(guestId, response);
		cartService.removeOne(guestId, bookId);
		return "redirect:/cart";
	}

	// === 선택 삭제 ===
	@DeleteMapping("/selected")
	public String removeSelected(@RequestParam("bookIds") List<Long> bookIds,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		HttpServletResponse response) {

		validateGuestId(guestId, response);
		cartService.removeSelected(guestId, bookIds);
		return "redirect:/cart";
	}

	// === 주문 페이지 - 선택된 도서 정보 렌더링 ===
	// Order 컨트롤러에서 바로 받기
	// @PostMapping("/order")
	// public String getSelectedCartItemsForOrder(
	// 	@CookieValue(value = "GUEST_ID", required = false) String guestId,
	// 	@RequestParam("bookIds") List<Long> bookIds,
	// 	Model model
	// ) {
	// 	List<BookCartOrderResponse> selectedItems = cartService.getSelectedCartItemsForOrder(guestId, bookIds);
	// 	model.addAttribute("selectedItems", selectedItems);
	// 	return "order/create";
	// }

	// === 보조 메서드 ===
	private void validateGuestId(String guestId, HttpServletResponse response) {
		if (guestId == null || guestId.isBlank()) {
			log.warn("GUEST_ID 쿠키가 존재하지 않습니다.");
			throw new GuestUserNotFoundException("guestId가 존재하지 않습니다. 쿠키를 발급받아야 합니다.");
		}
	}
}