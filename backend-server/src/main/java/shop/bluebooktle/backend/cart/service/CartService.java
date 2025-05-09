package shop.bluebooktle.backend.cart.service;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.common.entity.auth.User;

public interface CartService {

	Cart getCartByUser(User user, String guestId);

	void addBookToCart(User user, String guestId, Book book, int quantity);

	void removeBookFromCart(User user, String guestId, Book book);

	void clearCart(User user, String guestId);

}