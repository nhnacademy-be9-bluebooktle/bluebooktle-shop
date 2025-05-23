package shop.bluebooktle.frontend.service;

import java.util.List;

import shop.bluebooktle.common.dto.cart.response.CartItemResponse;

public interface CartService {

	// === 비회원 전용 ===
	void addToGuestCart(String guestId, Long bookId, int quantity);

	List<CartItemResponse> getGuestCartItems(String guestId);

	Void increaseGuestQuantity(String guestId, Long bookId);

	Void decreaseGuestQuantity(String guestId, Long bookId);

	Void removeOneFromGuestCart(String guestId, Long bookId);

	Void removeSelectedFromGuestCart(String guestId, List<Long> bookIds);

	// === 회원 전용 ===
	Void addToMemberCart(Long bookId, int quantity);

	List<CartItemResponse> getMemberCartItems();

	Void increaseMemberQuantity(Long bookId);

	Void decreaseMemberQuantity(Long bookId);

	Void removeOneFromMemberCart(Long bookId);

	Void removeSelectedFromMemberCart(List<Long> bookIds);

	// === 전환 및 병합 ===
	Void convertGuestCartToMember(String guestId);

	Void mergeGuestCartToMember(String guestId);

}
