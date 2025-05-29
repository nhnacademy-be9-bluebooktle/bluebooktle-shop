package shop.bluebooktle.frontend.service;

import java.util.List;

import shop.bluebooktle.common.dto.cart.response.CartItemResponse;

public interface CartService {

	// 공통: 장바구니에 도서 추가
	void addToCart(String guestId, Long bookId, int quantity);

	// 공통: 장바구니 조회
	List<CartItemResponse> getCartItems(String guestId);

	// 공통: 수량 증가
	void increaseQuantity(String guestId, Long bookId);

	// 공통: 수량 감소
	void decreaseQuantity(String guestId, Long bookId);

	// 공통: 도서 1개 삭제
	void removeOne(String guestId, Long bookId);

	// 공통: 선택 도서 여러 개 삭제
	void removeSelected(String guestId, List<Long> bookIds);

	// 전환: 회원가입 직후
	void convertGuestCartToMember(String guestId);

	// 병합: 로그인 직후
	void mergeGuestCartToMember(String guestId);
}