package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.cart.request.GuestCartItemRequest;
import shop.bluebooktle.common.dto.cart.request.GuestCartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.GuestCartRemoveSelectedRequest;
import shop.bluebooktle.common.dto.cart.request.MemberCartItemRequest;
import shop.bluebooktle.common.dto.cart.request.MemberCartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.MemberCartRemoveSelectedRequest;
import shop.bluebooktle.common.dto.cart.response.CartItemResponse;
import shop.bluebooktle.frontend.repository.CartRepository;
import shop.bluebooktle.frontend.service.CartService;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;

	// === 비회원 ===
	@Override
	public void addToGuestCart(String guestId, Long bookId, int quantity) {
		cartRepository.addBookToGuestCart(new GuestCartItemRequest(guestId, bookId, quantity));
	}

	@Override
	public List<CartItemResponse> getGuestCartItems(String guestId) {
		return cartRepository.getGuestCartItems(guestId);
	}

	@Override
	public Void increaseGuestQuantity(String guestId, Long bookId) {
		cartRepository.increaseGuestQuantity(new GuestCartItemRequest(guestId, bookId, 1));
		return null;
	}

	@Override
	public Void decreaseGuestQuantity(String guestId, Long bookId) {
		cartRepository.decreaseGuestQuantity(new GuestCartItemRequest(guestId, bookId, 1));
		return null;
	}

	@Override
	public Void removeOneFromGuestCart(String guestId, Long bookId) {
		cartRepository.removeBookFromGuestCart(new GuestCartRemoveOneRequest(guestId, bookId));
		return null;
	}

	@Override
	public Void removeSelectedFromGuestCart(String guestId, List<Long> bookIds) {
		cartRepository.removeSelectedBooksFromGuestCart(new GuestCartRemoveSelectedRequest(guestId, bookIds));
		return null;
	}

	@Override
	public Void convertGuestCartToMember(String guestId) {
		cartRepository.convertGuestCartToMember(guestId);
		return null;
	}

	@Override
	public Void mergeGuestCartToMember(String guestId) {
		cartRepository.mergeGuestCartToMember(guestId);
		return null;
	}

	// === 회원 ===
	@Override
	public Void addToMemberCart(Long bookId, int quantity) {
		cartRepository.addBookToMemberCart(new MemberCartItemRequest(bookId, quantity));
		return null;
	}

	@Override
	public List<CartItemResponse> getMemberCartItems() {
		return cartRepository.getMemberCartItems();
	}

	@Override
	public Void increaseMemberQuantity(Long bookId) {
		cartRepository.increaseMemberQuantity(new MemberCartItemRequest(bookId, 1));
		return null;
	}

	@Override
	public Void decreaseMemberQuantity(Long bookId) {
		cartRepository.decreaseMemberQuantity(new MemberCartItemRequest(bookId, 1));
		return null;
	}

	@Override
	public Void removeOneFromMemberCart(Long bookId) {
		cartRepository.removeBookFromMemberCart(new MemberCartRemoveOneRequest(bookId));
		return null;
	}

	@Override
	public Void removeSelectedFromMemberCart(List<Long> bookIds) {
		cartRepository.removeSelectedBooksFromMemberCart(new MemberCartRemoveSelectedRequest(bookIds));
		return null;
	}

}
