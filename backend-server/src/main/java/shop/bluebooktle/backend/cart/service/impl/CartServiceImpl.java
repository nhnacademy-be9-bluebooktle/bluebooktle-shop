package shop.bluebooktle.backend.cart.service.impl;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.cart.dto.CartItemRequest;
import shop.bluebooktle.backend.cart.dto.CartItemResponse;
import shop.bluebooktle.backend.cart.dto.CartOrderRequest;
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
	private final RedisTemplate<String, CartItemRequest> guestCartTemplate;
	// TODO BookService 아직 미규현
	// private final BookService bookService;

	private String guestCartKey(String guestId) {
		return "guest:" + guestId + ":cart";
	}

	@Override
	public Cart getCartByUser(User user) {
		return cartRepository.findByUser(user)
			.orElseThrow(() -> new RuntimeException("장바구니가 존재하지 않습니다."));
	}

	@Override
	public List<CartItemResponse> getCartItemsForUser(User user) {
		Cart cart = getCartByUser(user);
		return cart.getCartBooks().stream()
			.map(cb -> new CartItemResponse(cb.getBook().getId(), cb.getQuantity()))
			.collect(Collectors.toList());
	}

	@Override
	public void addBookToUserCart(User user, Book book, int quantity) {
		Cart cart = getCartByUser(user);
		Optional<CartBook> existing = cartBookRepository.findByCartAndBook(cart, book);

		if (existing.isPresent()) {
			cartBookRepository.addQuantity(cart, book, quantity);
		} else {
			CartBook cartBook = CartBook.builder()
				.cart(cart)
				.book(book)
				.quantity(quantity)
				.build();
			cartBookRepository.save(cartBook);
		}
	}

	@Override
	public void removeBookFromUserCart(User user, Book book) {
		Cart cart = getCartByUser(user);
		cartBookRepository.findByCartAndBook(cart, book)
			.ifPresent(cartBookRepository::delete);
	}

	@Override
	public void decreaseUserQuantity(User user, Book book) {
		Cart cart = getCartByUser(user);
		cartBookRepository.findByCartAndBook(cart, book).ifPresent(cartBook -> {
			if (cartBook.decreaseQuantity()) {
				cartBookRepository.delete(cartBook);
			}
		});
	}

	@Override
	public void clearUserCart(User user) {
		Cart cart = getCartByUser(user);
		cartBookRepository.deleteAll(cart.getCartBooks());
		cart.getCartBooks().clear();
	}

	@Override
	public void orderUserCartItems(User user, List<CartOrderRequest.OrderItem> items) {
		Cart cart = getCartByUser(user);
		for (CartOrderRequest.OrderItem item : items) {
			cartBookRepository.findByCartAndBook(cart, new Book(item.getBookId()))
				.ifPresent(cartBookRepository::delete);
		}
	}

	@Override
	public List<CartItemResponse> getCartItemsForGuest(String guestId) {
		HashOperations<String, String, CartItemRequest> hashOps = guestCartTemplate.opsForHash();
		return hashOps.values(guestCartKey(guestId)).stream()
			.map(i -> new CartItemResponse(i.getBookId(), i.getQuantity()))
			.collect(Collectors.toList());
	}

	@Override
	public void addBookToGuestCart(String guestId, Book book, int quantity) {
		String key = guestCartKey(guestId);
		HashOperations<String, String, CartItemRequest> hashOps = guestCartTemplate.opsForHash();

		CartItemRequest existing = hashOps.get(key, book.getId().toString());
		int newQuantity = (existing != null ? existing.getQuantity() : 0) + quantity;

		hashOps.put(key, book.getId().toString(), new CartItemRequest(book.getId(), newQuantity));
		guestCartTemplate.expire(key, Duration.ofDays(2));
	}

	@Override
	public void removeBookFromGuestCart(String guestId, Book book) {
		guestCartTemplate.opsForHash().delete(guestCartKey(guestId), book.getId().toString());
	}

	@Override
	public void decreaseGuestQuantity(String guestId, Book book) {
		String key = guestCartKey(guestId);
		HashOperations<String, String, CartItemRequest> hashOps = guestCartTemplate.opsForHash();
		CartItemRequest item = hashOps.get(key, book.getId().toString());

		if (item != null) {
			if (item.getQuantity() > 1) {
				item.setQuantity(item.getQuantity() - 1);
				hashOps.put(key, book.getId().toString(), item);
			} else {
				hashOps.delete(key, book.getId().toString());
			}
		}
	}

	@Override
	public void clearGuestCart(String guestId) {
		guestCartTemplate.delete(guestCartKey(guestId));
	}

	@Override
	public void orderGuestCartItems(String guestId, List<CartOrderRequest.OrderItem> items) {
		String key = guestCartKey(guestId);
		for (CartOrderRequest.OrderItem item : items) {
			guestCartTemplate.opsForHash().delete(key, item.getBookId().toString());
		}
	}

	@Override
	public void convertGuestCartToMember(String guestId, User user) {
		String key = guestCartKey(guestId);
		List<CartItemRequest> guestItems = guestCartTemplate.opsForHash().values(key);

		Cart cart = Cart.builder().user(user).build();
		cartRepository.save(cart);

		for (CartItemRequest item : guestItems) {
			Book book = bookService.findById(item.getBookId());
			CartBook cartBook = CartBook.builder()
				.cart(cart)
				.book(book)
				.quantity(item.getQuantity())
				.build();
			cartBookRepository.save(cartBook);
		}

		guestCartTemplate.delete(key);
	}

	@Override
	public void mergeGuestCartToMemberCart(String guestId, User user) {
		String key = guestCartKey(guestId);
		List<CartItemRequest> guestItems = guestCartTemplate.opsForHash().values(key);

		Cart cart = cartRepository.findByUser(user)
			.orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

		for (CartItemRequest item : guestItems) {
			Book book = bookService.findById(item.getBookId());
			Optional<CartBook> existing = cartBookRepository.findByCartAndBook(cart, book);

			if (existing.isPresent()) {
				existing.get().setQuantity(existing.get().getQuantity() + item.getQuantity());
			} else {
				CartBook cartBook = CartBook.builder()
					.cart(cart)
					.book(book)
					.quantity(item.getQuantity())
					.build();
				cartBookRepository.save(cartBook);
			}
		}

		guestCartTemplate.delete(key);
	}
}