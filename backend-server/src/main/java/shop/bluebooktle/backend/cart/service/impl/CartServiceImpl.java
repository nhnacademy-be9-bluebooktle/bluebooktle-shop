package shop.bluebooktle.backend.cart.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.cart.entity.CartBook;
import shop.bluebooktle.backend.cart.repository.CartBookRepository;
import shop.bluebooktle.backend.cart.repository.CartRepository;
import shop.bluebooktle.backend.cart.repository.redis.GuestCartRepository;
import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.cart.response.CartItemResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.cart.CartNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartBookRepository cartBookRepository;
	private final BookRepository bookRepository;
	private final GuestCartRepository guestCartRepository;
	private final UserRepository userRepository;

	@PersistenceContext
	private EntityManager em;

	// ----------------- 회원용 -----------------

	@Override
	@Transactional
	public User findUserEntityById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
	}

	@Override
	@Transactional
	public void addBookToUserCart(User user, Long bookId, int quantity) {
		Cart cart = getOrCreateCart(user);
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		cartBookRepository.findByCartAndBook(cart, book)
			.ifPresentOrElse(
				cartBook -> cartBook.increaseQuantity(quantity),
				() -> cartBookRepository.save(CartBook.of(book, cart, quantity))
			);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartItemResponse> getUserCartItems(User user) {
		Cart cart = cartRepository.findByUser(user)
			.orElseThrow(UserNotFoundException::new);

		return cart.getCartBooks().stream()
			.map(cb -> new CartItemResponse(cb.getBook().getId(), cb.getQuantity()))
			.toList();
	}

	@Override
	@Transactional
	public void increaseUserQuantity(User user, Long bookId, int quantity) {
		Cart cart = cartRepository.findByUser(user)
			.orElseGet(() -> cartRepository.save(new Cart(user)));
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		cartBookRepository.findByCartAndBook(cart, book)
			.ifPresentOrElse(
				cartBook -> cartBook.increaseQuantity(quantity), // ← 객체 메서드 호출 (OK)
				() -> cartBookRepository.save(CartBook.of(book, cart, quantity))
			);
	}

	@Override
	public void decreaseUserQuantity(User user, Long bookId, int quantity) {
		Cart cart = cartRepository.findByUser(user)
			.orElseThrow(CartNotFoundException::new);
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		cartBookRepository.findByCartAndBook(cart, book)
			.ifPresent(cartBook -> cartBook.decreaseQuantity(quantity));
	}

	@Override
	@Transactional
	public void removeBookFromUserCart(User user, Long bookId) {
		// Cart cart = cartRepository.findByUser(user)
		// 	.orElseThrow(CartNotFoundException::new);
		// Book book = bookRepository.findById(bookId)
		// 	.orElseThrow(BookNotFoundException::new);

		// CartBook cartBook = cartBookRepository.findByCartAndBook(cart, book);
		// if (cartBook != null) {
		// 	CartBook managed = cartBookRepository.findById(cartBook.getId())
		// 		.orElseThrow(() -> new RuntimeException("CartBook not found"));
		// 	cartBookRepository.delete(managed);
		// }
		Long cartBookId = 25L; // ← 반드시 L 붙이기 (Long 리터럴)
		CartBook cartBook = cartBookRepository.findById(cartBookId)
			.orElseThrow(() -> new RuntimeException("CartBook not found"));

		cartBookRepository.delete(cartBook);
	}

	@Override
	@Transactional
	public void removeSelectedBooksFromUserCart(User user, List<Long> bookIds) {
		Cart cart = cartRepository.findByUser(user)
			.orElseThrow(CartNotFoundException::new);

		List<CartBook> nonManaged = cartBookRepository.findAllByCartAndBookIdIn(cart, bookIds);

		List<CartBook> managedList = nonManaged.stream()
			.map(CartBook::getId)
			.map(cartBookRepository::findById)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.toList();

		cartBookRepository.deleteAll(managedList);
	}

	// ----------------- 비회원용 -----------------

	@Override
	@Transactional
	public void addBookToGuestCart(String guestId, Long bookId, int quantity) {
		guestCartRepository.addBook(guestId, bookId, quantity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartItemResponse> getGuestCartItems(String guestId) {
		return guestCartRepository.getCart(guestId).entrySet().stream()
			.map(entry -> new CartItemResponse(entry.getKey(), entry.getValue()))
			.toList();
	}

	@Override
	@Transactional
	public void increaseGuestQuantity(String guestId, Long bookId, int quantity) {
		guestCartRepository.increaseQuantity(guestId, bookId, quantity);
	}

	@Override
	@Transactional
	public void decreaseGuestQuantity(String guestId, Long bookId, int quantity) {
		guestCartRepository.decreaseQuantity(guestId, bookId, quantity);
	}

	@Override
	@Transactional
	public void removeBookFromGuestCart(String guestId, Long bookId) {
		guestCartRepository.removeBook(guestId, bookId);
	}

	@Override
	@Transactional
	public void removeSelectedBooksFromGuestCart(String guestId, List<Long> bookIds) {
		guestCartRepository.removeSelectedBooks(guestId, bookIds);
	}

	// ----------------- 공통 -----------------

	private Cart getOrCreateCart(User user) {
		return cartRepository.findByUser(user)
			.orElseGet(() -> cartRepository.save(
				Cart.builder()
					.user(user)
					.build()
			));
	}

	@Override
	@Transactional
	public void convertGuestCartToMemberCart(String guestId, User user) {
		Map<Long, Integer> guestCart = guestCartRepository.getCart(guestId);
		if (guestCart.isEmpty()) {
		}

		Cart cart = cartRepository.save(Cart.builder().user(user).build());

		List<CartBook> cartBooks = guestCart.entrySet().stream()
			.map(entry -> {
				Long bookId = entry.getKey();
				int quantity = entry.getValue();
				Book book = bookRepository.findById(bookId)
					.orElseThrow(BookNotFoundException::new);
				return CartBook.of(book, cart, quantity);
			})
			.toList();

		cartBookRepository.saveAll(cartBooks);

		// Redis 장바구니 제거 (옵션)
		guestCartRepository.removeSelectedBooks(guestId, guestCart.keySet().stream().toList());
	}

	@Override
	@Transactional
	public void mergeGuestCartToMemberCart(String guestId, User user) {
		Map<Long, Integer> guestCart = guestCartRepository.getCart(guestId);
		if (guestCart.isEmpty()) {
		}

		Cart cart = getOrCreateCart(user);

		for (Map.Entry<Long, Integer> entry : guestCart.entrySet()) {
			Long bookId = entry.getKey();
			int guestQuantity = entry.getValue();
			Book book = bookRepository.findById(bookId)
				.orElseThrow(BookNotFoundException::new);

			cartBookRepository.findByCartAndBook(cart, book)
				.ifPresentOrElse(
					existing -> existing.increaseQuantity(guestQuantity),
					() -> cartBookRepository.save(CartBook.of(book, cart, guestQuantity))
				);
		}

		// Redis 비우기
		guestCartRepository.removeSelectedBooks(guestId, guestCart.keySet().stream().toList());
	}

}