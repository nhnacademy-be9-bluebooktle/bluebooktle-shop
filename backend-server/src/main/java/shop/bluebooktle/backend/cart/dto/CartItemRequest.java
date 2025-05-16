package shop.bluebooktle.backend.cart.dto;

public record CartItemRequest(
	Long bookId,
	int quantity
) {
}