package shop.bluebooktle.backend.cart.service;

import java.util.List;

import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.entity.auth.User;

public interface CartService {
	// 회원용

	User findUserEntityById(Long userId);

	void addBookToUserCart(User user, Long bookId, int quantity);

	List<BookCartOrderResponse> getUserCartItems(User user);

	void increaseUserQuantity(User user, Long bookId, int quantity);

	void decreaseUserQuantity(User user, Long bookId, int quantity);

	void removeBookFromUserCart(User user, Long bookId);

	void removeSelectedBooksFromUserCart(User user, List<Long> bookIds);

	List<BookCartOrderResponse> sendSelectedCartItemsToOrder(User user, List<Long> bookIds);

	Long getCartSize(User user);

	// 비회원용
	void addBookToGuestCart(String guestId, Long bookId, int quantity);

	List<BookCartOrderResponse> getGuestCartItems(String guestId);

	void increaseGuestQuantity(String guestId, Long bookId, int quantity);

	void decreaseGuestQuantity(String guestId, Long bookId, int quantity);

	void removeBookFromGuestCart(String guestId, Long bookId);

	void removeSelectedBooksFromGuestCart(String guestId, List<Long> bookIds);

	List<BookCartOrderResponse> sendSelectedGuestCartItemsToOrder(String guestId, List<Long> bookIds);

	Long getGuestCartSize(String guestId);

	// ----------------- 전환용 -----------------

	void mergeOrConvertGuestCartToMemberCart(String guestId, User user);
}

