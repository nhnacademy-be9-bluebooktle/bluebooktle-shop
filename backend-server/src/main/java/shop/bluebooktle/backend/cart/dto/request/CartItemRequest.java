package shop.bluebooktle.backend.cart.dto.request;

public record CartItemRequest(
	Long bookId,
	int quantity
) {
}