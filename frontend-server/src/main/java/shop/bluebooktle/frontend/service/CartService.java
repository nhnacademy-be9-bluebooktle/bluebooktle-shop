package shop.bluebooktle.frontend.service;

import java.util.List;

import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;

public interface CartService {

	// 공통: 장바구니에 도서 추가
	void addToCart(String guestId, Long bookId, int quantity);

	// 공통: 장바구니 조회
	List<BookCartOrderResponse> getCartItems(String guestId);

	// 공통: 수량 증가
	void increaseQuantity(String guestId, Long bookId);

	// 공통: 수량 감소
	void decreaseQuantity(String guestId, Long bookId);

	// 공통: 도서 1개 삭제
	void removeOne(String guestId, Long bookId);

	// 공통: 선택 도서 여러 개 삭제
	void removeSelected(String guestId, List<Long> bookIds);

	List<BookCartOrderResponse> getSelectedCartItemsForOrder(String guestId, List<Long> bookIds);

	void mergeOrConvertGuestCart(String guestId);

	Long getCartSize(String guestId);
}