package shop.bluebooktle.common.dto.cart.request;

public record GuestCartRemoveOneRequest(
	String id,
	Long bookId
) {
}
