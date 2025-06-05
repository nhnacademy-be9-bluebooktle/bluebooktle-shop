package shop.bluebooktle.backend.cart.repository.redis;

import java.util.List;
import java.util.Map;

public interface GuestCartRepository {
	void addBook(String guestId, Long bookId, int quantity);

	void increaseQuantity(String guestId, Long bookId, int quantity);

	void decreaseQuantity(String guestId, Long bookId, int quantity);

	void removeBook(String guestId, Long bookId);

	void removeSelectedBooks(String guestId, List<Long> bookIds);

	Map<Long, Integer> getCart(String guestId);

	Long getCartSize(String guestId);
}