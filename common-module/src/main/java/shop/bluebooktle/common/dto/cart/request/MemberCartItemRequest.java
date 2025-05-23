package shop.bluebooktle.common.dto.cart.request;

public record MemberCartItemRequest(
	Long bookId,
	int quantity
) {
}