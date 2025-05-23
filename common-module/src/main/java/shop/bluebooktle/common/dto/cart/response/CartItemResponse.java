package shop.bluebooktle.common.dto.cart.response;

public record CartItemResponse(
	Long bookId,
	int quantity
) {
}
