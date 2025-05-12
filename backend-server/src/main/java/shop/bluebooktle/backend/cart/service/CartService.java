package shop.bluebooktle.backend.cart.service;

import java.util.List;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.cart.dto.CartItemResponse;
import shop.bluebooktle.backend.cart.dto.CartOrderRequest;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.common.entity.auth.User;

public interface CartService {

	// 회원 전용
	Cart getCartByUser(User user);

	List<CartItemResponse> getCartItemsForUser(User user);

	void addBookToUserCart(User user, Book book, int quantity);

	void removeBookFromUserCart(User user, Book book);

	void decreaseUserQuantity(User user, Book book);

	void clearUserCart(User user);

	void orderUserCartItems(User user, List<CartOrderRequest.OrderItem> items);

	// 비회원 전용
	List<CartItemResponse> getCartItemsForGuest(String guestId);

	void addBookToGuestCart(String guestId, Book book, int quantity);

	void removeBookFromGuestCart(String guestId, Book book);

	void decreaseGuestQuantity(String guestId, Book book);

	void clearGuestCart(String guestId);

	void orderGuestCartItems(String guestId, List<CartOrderRequest.OrderItem> items);

	// 회원가입 또는 로그인 시 병합/전환 처리
	void convertGuestCartToMember(String guestId, User user);

	void mergeGuestCartToMemberCart(String guestId, User user);
}
