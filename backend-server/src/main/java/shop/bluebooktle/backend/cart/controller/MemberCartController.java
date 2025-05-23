package shop.bluebooktle.backend.cart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.common.dto.cart.request.MemberCartItemRequest;
import shop.bluebooktle.common.dto.cart.request.MemberCartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.MemberCartRemoveSelectedRequest;
import shop.bluebooktle.common.dto.cart.response.CartItemResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.security.UserPrincipal;

@RestController
@RequestMapping("/api/cart/member")
@RequiredArgsConstructor
public class MemberCartController {

	private final CartService cartService;

	private User getAuthenticatedUser(UserPrincipal principal) {
		if (principal == null) {
			throw new UserNotFoundException();
		}
		return cartService.findUserEntityById(principal.getUserId());
	}

	@PostMapping
	public ResponseEntity<JsendResponse<Void>> addBookToCart(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody MemberCartItemRequest request
	) {
		User user = getAuthenticatedUser(userPrincipal);
		cartService.addBookToUserCart(user, request.bookId(), request.quantity());
		return ResponseEntity.ok(JsendResponse.success());
	}

	@GetMapping
	public ResponseEntity<JsendResponse<List<CartItemResponse>>> getCartItems(
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		User user = getAuthenticatedUser(userPrincipal);
		List<CartItemResponse> result = cartService.getUserCartItems(user);
		return ResponseEntity.ok(JsendResponse.success(result));
	}

	@PatchMapping("/increase")
	public ResponseEntity<JsendResponse<Void>> increaseQuantity(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody MemberCartItemRequest request
	) {
		User user = getAuthenticatedUser(userPrincipal);
		cartService.increaseUserQuantity(user, request.bookId(), request.quantity());
		return ResponseEntity.ok(JsendResponse.success());
	}

	@PatchMapping("/decrease")
	public ResponseEntity<JsendResponse<Void>> decreaseQuantity(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody MemberCartItemRequest request
	) {
		User user = getAuthenticatedUser(userPrincipal);
		cartService.decreaseUserQuantity(user, request.bookId(), request.quantity());
		return ResponseEntity.ok(JsendResponse.success());
	}

	@DeleteMapping
	public ResponseEntity<JsendResponse<Void>> removeBook(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody MemberCartRemoveOneRequest request
	) {
		User user = getAuthenticatedUser(userPrincipal);
		cartService.removeBookFromUserCart(user, request.bookId());
		return ResponseEntity.ok(JsendResponse.success());
	}

	@DeleteMapping("/selected")
	public ResponseEntity<JsendResponse<Void>> removeSelectedBooks(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody MemberCartRemoveSelectedRequest request
	) {
		User user = getAuthenticatedUser(userPrincipal);
		cartService.removeSelectedBooksFromUserCart(user, request.bookIds());
		return ResponseEntity.ok(JsendResponse.success());
	}
}