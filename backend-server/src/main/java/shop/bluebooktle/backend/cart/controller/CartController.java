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
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.cart.request.BookIdListRequest;
import shop.bluebooktle.common.dto.cart.request.CartItemRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveSelectedRequest;
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
			return "UnIdentified_GUEST";
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
	public ResponseEntity<JsendResponse<List<BookCartOrderResponse>>> getCartItems(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestHeader(value = "GUEST_ID", required = false) String guestId
	) {
		if (principal == null) {
			List<BookCartOrderResponse> items = cartService.getGuestCartItems(validateGuestId(guestId));
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

	/** 공통: 주문용 선택된 장바구니 도서 조회 */
	@PostMapping("/order")
	public ResponseEntity<JsendResponse<List<BookCartOrderResponse>>> getSelectedCartItemsForOrder(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestHeader(value = "GUEST_ID", required = false) String guestId,
		@RequestBody BookIdListRequest request
	) {
		if (principal == null) {
			List<BookCartOrderResponse> items =
				cartService.sendSelectedGuestCartItemsToOrder(validateGuestId(guestId), request.bookIds());
			return ResponseEntity.ok(JsendResponse.success(items));
		} else {
			User user = getAuthenticatedUser(principal);
			List<BookCartOrderResponse> items = cartService.sendSelectedCartItemsToOrder(user, request.bookIds());
			return ResponseEntity.ok(JsendResponse.success(items));
		}
	}

	/** 전환 또는 병합: 회원가입 또는 로그인 직후 */
	@PostMapping("/convert/merge")
	public ResponseEntity<JsendResponse<Void>> mergeOrConvertGuestCart(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestHeader("GUEST_ID") String guestId
	) {
		User user = getAuthenticatedUser(principal);
		cartService.mergeOrConvertGuestCartToMemberCart(guestId, user);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@GetMapping("quantity")
	public ResponseEntity<JsendResponse<Long>> getQuantity(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestHeader(value = "GUEST_ID", required = false) String guestId
	) {
		Long size;
		if (principal == null) {
			size = cartService.getGuestCartSize(guestId);
		} else {
			User user = getAuthenticatedUser(principal);
			size = cartService.getCartSize(user);
		}
		return ResponseEntity.ok(JsendResponse.success(size));
	}
}
