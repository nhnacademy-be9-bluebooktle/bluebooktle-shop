package shop.bluebooktle.backend.cart.service;

import java.util.List;

import shop.bluebooktle.common.dto.cart.response.CartItemResponse;
import shop.bluebooktle.common.entity.auth.User;

public interface CartService {
	// 회원용

	User findUserEntityById(Long userId);

	void addBookToUserCart(User user, Long bookId, int quantity);

	List<CartItemResponse> getUserCartItems(User user);

	void increaseUserQuantity(User user, Long bookId, int quantity);

	void decreaseUserQuantity(User user, Long bookId, int quantity);

	void removeBookFromUserCart(User user, Long bookId);

	void removeSelectedBooksFromUserCart(User user, List<Long> bookIds);

	// 비회원용
	void addBookToGuestCart(String guestId, Long bookId, int quantity);

	List<CartItemResponse> getGuestCartItems(String guestId);

	void increaseGuestQuantity(String guestId, Long bookId, int quantity);

	void decreaseGuestQuantity(String guestId, Long bookId, int quantity);

	void removeBookFromGuestCart(String guestId, Long bookId);

	void removeSelectedBooksFromGuestCart(String guestId, List<Long> bookIds);

	// ----------------- 전환용 -----------------

	// 회원가입 직후: Redis → DB로 옮기기 (기존 장바구니 없음)
	void convertGuestCartToMemberCart(String guestId, User user);

	// 로그인 시점: Redis → DB로 병합 (기존 장바구니 있음)
	void mergeGuestCartToMemberCart(String guestId, User user);
}

