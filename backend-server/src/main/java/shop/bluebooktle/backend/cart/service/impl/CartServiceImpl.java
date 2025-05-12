package shop.bluebooktle.backend.cart.service.impl;

import java.util.List;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.backend.cart.dto.CartItemRequest;
import shop.bluebooktle.backend.cart.dto.CartItemResponse;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.cart.entity.CartBook;
import shop.bluebooktle.backend.cart.repository.CartBookRepository;
import shop.bluebooktle.backend.cart.repository.CartRepository;
import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.common.entity.auth.User;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartBookRepository cartBookRepository;
	private final RedisTemplate<String, CartItemRequest> redisTemplate;
	private final BookService bookService;
	private static final String GUEST_KEY_PREFIX = "guest:";

	// ----------------- 회원용 -----------------

	@Override
	@Transactional
	public void addBookToUserCart(User user, Long bookId, int quantity) {
		Cart cart = getOrCreateCart(user);
		Book book = bookService.getBookById(bookId);

		CartBook cartBook = cartBookRepository.findByCartAndBook(cart, book)
			.orElseGet(() -> new CartBook(book, cart, 0));

		cartBook.increaseQuantity(quantity);
		cartBookRepository.save(cartBook);
	}

	// TODO BookInfoService 완성되면 연결
	@Override
	@Transactional(readOnly = true)
	public List<CartItemResponse> getUserCartItems(User user) {
		Cart cart = getOrCreateCart(user);
		List<CartBook> cartBooks = cartBookRepository.findAllByCart(cart);

		// return cartBooks.stream()
		// 	.map(cartBook -> {
		// 		Book book = bookService.getBookById(cartBook.getBook().getId());
		// 		return CartItemResponse.from(cartBook, book);
		// 	})
		// 	.toList();
		return null;
	}

	@Override
	@Transactional
	public void increaseUserQuantity(User user, Long bookId) {
		Cart cart = getOrCreateCart(user);
		Book book = bookService.getBookById(bookId);

		cartBookRepository.findByCartAndBook(cart, book)
			.ifPresent(cartBook -> {
				cartBook.increaseQuantity(1);
				cartBookRepository.save(cartBook);
			});
	}

	@Override
	@Transactional
	public void decreaseUserQuantity(User user, Long bookId) {
		Cart cart = getOrCreateCart(user);
		Book book = bookService.getBookById(bookId);

		cartBookRepository.findByCartAndBook(cart, book)
			.ifPresent(cartBook -> {
				if (cartBook.getQuantity() > 1) {
					cartBook.decreaseQuantity(1);
					cartBookRepository.save(cartBook);
				}
			});
	}

	@Override
	@Transactional
	public void removeBookFromUserCart(User user, Long bookId) {
		Cart cart = getOrCreateCart(user);
		Book book = bookService.getBookById(bookId);

		cartBookRepository.findByCartAndBook(cart, book)
			.ifPresent(cartBookRepository::delete);
	}

	@Override
	@Transactional
	public void removeSelectedBooksFromUserCart(User user, List<Long> bookIds) {
		Cart cart = getOrCreateCart(user);
		List<CartBook> selectedItems = cartBookRepository.findAllByCartAndBookIdIn(cart, bookIds);
		cartBookRepository.deleteAll(selectedItems);
	}

	// ----------------- 비회원용 -----------------

	@Override
	public void addBookToGuestCart(String guestId, Long bookId, int quantity) {
		HashOperations<String, String, CartItemRequest> ops = redisTemplate.opsForHash();
		String key = GUEST_KEY_PREFIX + guestId;
		String field = bookId.toString();

		CartItemRequest current = ops.get(key, field);
		int updatedQuantity = (current != null ? current.quantity() : 0) + quantity;

		ops.put(key, field, new CartItemRequest(bookId, updatedQuantity));
	}

	@Override
	public List<CartItemResponse> getGuestCartItems(String guestId) {
		HashOperations<String, String, CartItemRequest> ops = redisTemplate.opsForHash();
		String key = GUEST_KEY_PREFIX + guestId;

		// TODO BookInfoService 완성되면 연결
		// return ops.values(key).stream()
		// 	.map(req -> new CartItemResponse(req.bookId(), guestId))
		// 	.toList();
		return null;
	}

	@Override
	public void increaseGuestQuantity(String guestId, Long bookId) {
		addBookToGuestCart(guestId, bookId, 1);
	}

	@Override
	public void decreaseGuestQuantity(String guestId, Long bookId) {
		HashOperations<String, String, CartItemRequest> ops = redisTemplate.opsForHash();
		String key = GUEST_KEY_PREFIX + guestId;
		String field = bookId.toString();

		CartItemRequest current = ops.get(key, field);
		if (current != null && current.quantity() > 1) {
			ops.put(key, field, new CartItemRequest(bookId, current.quantity() - 1));
		}
	}

	@Override
	public void removeBookFromGuestCart(String guestId, Long bookId) {
		redisTemplate.opsForHash().delete(GUEST_KEY_PREFIX + guestId, bookId.toString());
	}

	@Override
	public void removeSelectedBooksFromGuestCart(String guestId, List<Long> bookIds) {
		String key = GUEST_KEY_PREFIX + guestId;
		Object[] fields = bookIds.stream().map(String::valueOf).toArray();
		redisTemplate.opsForHash().delete(key, fields);
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
		HashOperations<String, String, CartItemRequest> ops = redisTemplate.opsForHash();
		String guestKey = GUEST_KEY_PREFIX + guestId;

		List<CartItemRequest> guestItems = ops.values(guestKey);
		Cart cart = getOrCreateCart(user); // 회원 장바구니 DB

		for (CartItemRequest item : guestItems) {
			Book book = bookService.getBookById(item.bookId());

			cartBookRepository.findByCartAndBook(cart, book)
				.ifPresentOrElse(
					existing -> existing.increaseQuantity(item.quantity()),
					() -> cartBookRepository.save(new CartBook(book, cart, item.quantity()))
				);
		}

		redisTemplate.delete(guestKey); // 비회원 장바구니 제거
	}

	@Override
	@Transactional
	public void mergeGuestCartToMemberCart(String guestId, User user) {
		HashOperations<String, String, CartItemRequest> ops = redisTemplate.opsForHash();
		String guestKey = GUEST_KEY_PREFIX + guestId;

		List<CartItemRequest> guestItems = ops.values(guestKey);
		Cart cart = getOrCreateCart(user); // 기존 장바구니

		for (CartItemRequest item : guestItems) {
			Book book = bookService.getBookById(item.bookId());

			cartBookRepository.findByCartAndBook(cart, book)
				.ifPresentOrElse(
					existing -> existing.increaseQuantity(item.quantity()),
					() -> cartBookRepository.save(new CartBook(book, cart, item.quantity()))
				);
		}

		redisTemplate.delete(guestKey);
	}

}