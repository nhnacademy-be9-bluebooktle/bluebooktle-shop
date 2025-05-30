package shop.bluebooktle.backend.cart.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.common.dto.cart.request.CartItemRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveSelectedRequest;
import shop.bluebooktle.common.dto.cart.response.CartItemResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.security.UserPrincipal;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	private User getAuthenticatedUser(UserPrincipal principal) {
		if (principal == null) {
			throw new UserNotFoundException("로그인 정보가 없습니다.");
		}
		return cartService.findUserEntityById(principal.getUserId());
	}

	private String validateGuestId(String guestId) {
		if (guestId == null || guestId.isBlank()) {
			throw new IllegalArgumentException("유효하지 않은 비회원 식별자입니다.");
		}
		return guestId;
	}

	/** 공통: 장바구니 담기 */
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> addBookToCart(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestHeader(value = "GUEST_ID", required = false) String guestId,
		@RequestBody CartItemRequest request
	) {
		if (principal == null) {
			cartService.addBookToGuestCart(validateGuestId(guestId), request.bookId(), request.quantity());
		} else {
			User user = getAuthenticatedUser(principal);
			cartService.addBookToUserCart(user, request.bookId(), request.quantity());
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	/** 공통: 장바구니 조회 */
	@GetMapping
	public ResponseEntity<JsendResponse<List<CartItemResponse>>> getCartItems(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestHeader(value = "GUEST_ID", required = false) String guestId
	) {
		if (principal == null) {
			List<CartItemResponse> items = cartService.getGuestCartItems(validateGuestId(guestId));
			return ResponseEntity.ok(JsendResponse.success(items));
		} else {
			User user = getAuthenticatedUser(principal);
			return ResponseEntity.ok(JsendResponse.success(cartService.getUserCartItems(user)));
		}
	}

	/** 공통: 수량 증가 */
	@PostMapping("/increase")
	public ResponseEntity<JsendResponse<Void>> increaseQuantity(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestHeader(value = "GUEST_ID", required = false) String guestId,
		@RequestBody CartItemRequest request
	) {
		if (principal == null) {
			cartService.increaseGuestQuantity(validateGuestId(guestId), request.bookId(), request.quantity());
		} else {
			User user = getAuthenticatedUser(principal);
			cartService.increaseUserQuantity(user, request.bookId(), request.quantity());
		}
		return ResponseEntity.ok(JsendResponse.success());
	}

	/** 공통: 수량 감소 */
	@PostMapping("/decrease")
	public ResponseEntity<JsendResponse<Void>> decreaseQuantity(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestHeader(value = "GUEST_ID", required = false) String guestId,
		@RequestBody CartItemRequest request
	) {
		if (principal == null) {
			cartService.decreaseGuestQuantity(validateGuestId(guestId), request.bookId(), request.quantity());
		} else {
			User user = getAuthenticatedUser(principal);
			cartService.decreaseUserQuantity(user, request.bookId(), request.quantity());
		}
		return ResponseEntity.ok(JsendResponse.success());
	}

	/** 공통: 도서 1개 삭제 */
	@DeleteMapping
	public ResponseEntity<JsendResponse<Void>> removeBook(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestHeader(value = "GUEST_ID", required = false) String guestId,
		@RequestBody CartRemoveOneRequest request
	) {
		log.info("delete mapping start");
		if (principal == null) {
			cartService.removeBookFromGuestCart(validateGuestId(guestId), request.bookId());
		} else {
			User user = getAuthenticatedUser(principal);
			cartService.removeBookFromUserCart(user, request.bookId());
		}
		return ResponseEntity.ok(JsendResponse.success());
	}

	/** 공통: 선택 삭제 */
	@DeleteMapping("/selected")
	public ResponseEntity<JsendResponse<Void>> removeSelectedBooks(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestHeader(value = "GUEST_ID", required = false) String guestId,
		@RequestBody CartRemoveSelectedRequest request
	) {
		if (principal == null) {
			cartService.removeSelectedBooksFromGuestCart(validateGuestId(guestId), request.bookIds());
		} else {
			User user = getAuthenticatedUser(principal);
			cartService.removeSelectedBooksFromUserCart(user, request.bookIds());
		}
		return ResponseEntity.ok(JsendResponse.success());
	}

	/** 전환: 회원가입 직후 */
	@PostMapping("/convert/to-member")
	public ResponseEntity<JsendResponse<Void>> convertGuestToMemberCart(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestBody String guestId
	) {
		User user = getAuthenticatedUser(principal);
		cartService.convertGuestCartToMemberCart(guestId, user);
		return ResponseEntity.ok(JsendResponse.success());
	}

	/** 병합: 로그인 직후 */
	@PostMapping("/convert/merge")
	public ResponseEntity<JsendResponse<Void>> mergeGuestToMemberCart(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestBody String guestId
	) {
		User user = getAuthenticatedUser(principal);
		cartService.mergeGuestCartToMemberCart(guestId, user);
		return ResponseEntity.ok(JsendResponse.success());
	}
}