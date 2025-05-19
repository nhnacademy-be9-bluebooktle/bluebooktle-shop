package shop.bluebooktle.backend.cart.dto.response;

public record CartItemResponse(
	Long bookId,
	int quantity
) {
}
