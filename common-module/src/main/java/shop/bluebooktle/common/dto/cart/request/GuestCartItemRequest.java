package shop.bluebooktle.common.dto.cart.request;

public record GuestCartItemRequest(
	String id,   // guestId 또는 userId
	Long bookId,
	int quantity
) {
}