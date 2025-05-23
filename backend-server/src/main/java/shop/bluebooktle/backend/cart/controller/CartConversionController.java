package shop.bluebooktle.backend.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.principal.UserPrincipal;

@RestController
@RequestMapping("/api/cart/convert")
@RequiredArgsConstructor
public class CartConversionController {

	private final CartService cartService;

	private User getAuthenticatedUser(UserPrincipal principal) {
		if (principal == null) {
			throw new UserNotFoundException("로그인 정보가 없습니다.");
		}
		return cartService.findUserEntityById(principal.getUserId());
	}

	// 1. 회원가입 직후: 비회원 장바구니 → 회원 장바구니로 전환
	@PostMapping("/to-member")
	public ResponseEntity<JsendResponse<Void>> convertGuestToMemberCart(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody String guestId
	) {
		User user = getAuthenticatedUser(userPrincipal);
		cartService.convertGuestCartToMemberCart(guestId, user);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 2. 로그인 직후: 기존 회원 장바구니에 병합
	@PatchMapping("/merge")
	public ResponseEntity<JsendResponse<Void>> mergeGuestToMemberCart(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody String guestId
	) {
		User user = getAuthenticatedUser(userPrincipal);
		cartService.mergeGuestCartToMemberCart(guestId, user);
		return ResponseEntity.ok(JsendResponse.success());
	}
}