package shop.bluebooktle.backend.cart.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.common.dto.cart.request.GuestCartItemRequest;
import shop.bluebooktle.common.dto.cart.request.GuestCartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.GuestCartRemoveSelectedRequest;
import shop.bluebooktle.common.dto.cart.response.CartItemResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@Slf4j
@RestController
@RequestMapping("/api/cart/guest")
@RequiredArgsConstructor
public class GuestCartController {

	private final CartService cartService;

	private String validate(String guestId) {
		if (guestId == null || guestId.isBlank()) {
			throw new IllegalArgumentException("유효하지 않은 비회원 식별자입니다.");
		}
		return guestId;
	}

	// 1. 장바구니에 도서 추가
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> addBookToGuestCart(
		@RequestBody GuestCartItemRequest request
	) {
		cartService.addBookToGuestCart(validate(request.id()), request.bookId(), request.quantity());
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 2. 장바구니 목록 조회
	@GetMapping
	public ResponseEntity<JsendResponse<List<CartItemResponse>>> getGuestCartItems(
		@RequestParam String guestId
	) {
		List<CartItemResponse> items = cartService.getGuestCartItems(validate(guestId));
		return ResponseEntity.ok(JsendResponse.success(items));
	}

	// 3. 수량 증가
	@PatchMapping("/increase")
	public ResponseEntity<JsendResponse<Void>> increaseGuestQuantity(
		@RequestBody GuestCartItemRequest request
	) {
		cartService.increaseGuestQuantity(validate(request.id()), request.bookId(), request.quantity());
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 4. 수량 감소
	@PatchMapping("/decrease")
	public ResponseEntity<JsendResponse<Void>> decreaseGuestQuantity(
		@RequestBody GuestCartItemRequest request
	) {
		cartService.decreaseGuestQuantity(validate(request.id()), request.bookId(), request.quantity());
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 5. 도서 1개 삭제
	@DeleteMapping
	public ResponseEntity<JsendResponse<Void>> removeBookFromGuestCart(
		@RequestBody GuestCartRemoveOneRequest request
	) {
		cartService.removeBookFromGuestCart(validate(request.id()), request.bookId());
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 6. 선택한 도서 여러 개 삭제
	@DeleteMapping("/selected")
	public ResponseEntity<JsendResponse<Void>> removeSelectedBooksFromGuestCart(
		@RequestBody GuestCartRemoveSelectedRequest request
	) {
		cartService.removeSelectedBooksFromGuestCart(validate(request.id()), request.bookIds());
		return ResponseEntity.ok(JsendResponse.success());
	}
}