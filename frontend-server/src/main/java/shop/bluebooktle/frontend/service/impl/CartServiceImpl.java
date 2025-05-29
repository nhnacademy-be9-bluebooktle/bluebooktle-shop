package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.cart.request.CartItemRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveSelectedRequest;
import shop.bluebooktle.common.dto.cart.response.CartItemResponse;
import shop.bluebooktle.frontend.repository.CartRepository;
import shop.bluebooktle.frontend.service.CartService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;

	@Override
	public void addToCart(String guestId, Long bookId, int quantity) {
		log.debug("🧪 addToCart called with guestId={}, bookId={}, quantity={}", guestId, bookId, quantity);
		cartRepository.addBookToCart(new CartItemRequest(bookId, quantity), guestId);
	}

	@Override
	public List<CartItemResponse> getCartItems(String guestId) {
		return cartRepository.getCartItems(guestId);
	}

	@Override
	public void increaseQuantity(String guestId, Long bookId) {
		cartRepository.increaseQuantity(new CartItemRequest(bookId, 1), guestId);
	}

	@Override
	public void decreaseQuantity(String guestId, Long bookId) {
		cartRepository.decreaseQuantity(new CartItemRequest(bookId, 1), guestId);
	}

	@Override
	public void removeOne(String guestId, Long bookId) {
		cartRepository.removeBook(new CartRemoveOneRequest(guestId, bookId), guestId);
	}

	@Override
	public void removeSelected(String guestId, List<Long> bookIds) {
		cartRepository.removeSelectedBooks(new CartRemoveSelectedRequest(guestId, bookIds), guestId);
	}

	@Override
	public void convertGuestCartToMember(String guestId) {
		cartRepository.convertGuestCartToMember(guestId);
	}

	@Override
	public void mergeGuestCartToMember(String guestId) {
		cartRepository.mergeGuestCartToMember(guestId);
	}
}