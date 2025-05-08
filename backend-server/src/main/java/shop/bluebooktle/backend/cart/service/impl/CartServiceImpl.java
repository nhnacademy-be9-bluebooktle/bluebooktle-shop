package shop.bluebooktle.backend.cart.service.impl;

import java.util.Optional;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.cart.dto.CartItemRequest;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.cart.entity.CartBook;
import shop.bluebooktle.backend.cart.repository.CartBookRepository;
import shop.bluebooktle.backend.cart.repository.CartRepository;
import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.backend.user.entity.User;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartBookRepository cartBookRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public Cart getCartByUser(User user, String guestId) {
		if (user != null) {
			return cartRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("장바구니가 존재하지 않습니다."));
		} else if (guestId != null) {
			// TODO: 비회원 장바구니 조회 시 Redis 값 반환 형태 정의 필요
			throw new UnsupportedOperationException("비회원 장바구니 조회는 별도 메서드 사용 필요");
		} else {
			throw new IllegalArgumentException("사용자 정보가 없습니다.");
		}
	}

	@Override
	public void addBookToCart(User user, String guestId, Book book, int quantity) {
		if (user != null) {
			Cart cart = getCartByUser(user, null);
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
		} else if (guestId != null) {
			String key = guestCartKey(guestId);
			HashOperations<String, String, CartItemRequest> hashOps = redisTemplate.opsForHash();

			CartItemRequest existing = hashOps.get(key, book.getId().toString());
			int newQuantity = (existing != null ? existing.getQuantity() : 0) + quantity;

			hashOps.put(key, book.getId().toString(), new CartItemRequest(book.getId(), newQuantity));
		} else {
			throw new IllegalArgumentException("사용자 정보가 없습니다.");
		}
	}

	@Override
	public void removeBookFromCart(User user, String guestId, Book book) {
		if (user != null) {
			Cart cart = getCartByUser(user, null);
			cartBookRepository.findByCartAndBook(cart, book)
				.ifPresent(cartBookRepository::delete);
		} else if (guestId != null) {
			redisTemplate.opsForHash().delete(guestCartKey(guestId), book.getId().toString());
		} else {
			throw new IllegalArgumentException("사용자 정보가 없습니다.");
		}
	}

	@Override
	public void clearCart(User user, String guestId) {
		if (user != null) {
			Cart cart = getCartByUser(user, null);
			cartBookRepository.deleteAll(cart.getCartBooks());
			cart.getCartBooks().clear();
		} else if (guestId != null) {
			redisTemplate.delete(guestCartKey(guestId));
		} else {
			throw new IllegalArgumentException("사용자 정보가 없습니다.");
		}
	}

	private String guestCartKey(String guestId) {
		return "guest:" + guestId + ":cart";
	}
}
