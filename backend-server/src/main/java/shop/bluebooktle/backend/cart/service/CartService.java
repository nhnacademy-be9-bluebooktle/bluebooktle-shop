package shop.bluebooktle.backend.cart.service;

import java.util.List;

import shop.bluebooktle.backend.cart.dto.CartItemResponse;
import shop.bluebooktle.common.entity.auth.User;

public interface CartService {
	// 회원용
	void addBookToUserCart(User user, Long bookId, int quantity);

	List<CartItemResponse> getUserCartItems(User user);

	void increaseUserQuantity(User user, Long bookId);

	void decreaseUserQuantity(User user, Long bookId);

	void removeBookFromUserCart(User user, Long bookId);

	void removeSelectedBooksFromUserCart(User user, List<Long> bookIds);

	// 비회원용
	void addBookToGuestCart(String guestId, Long bookId, int quantity);

	List<CartItemResponse> getGuestCartItems(String guestId);

	void increaseGuestQuantity(String guestId, Long bookId);

	void decreaseGuestQuantity(String guestId, Long bookId);

	void removeBookFromGuestCart(String guestId, Long bookId);

	void removeSelectedBooksFromGuestCart(String guestId, List<Long> bookIds);

	public void convertGuestCartToMemberCart(String guestId, User user);

	public void mergeGuestCartToMemberCart(String guestId, User user);

}

