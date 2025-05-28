package shop.bluebooktle.common.dto.order.request;

public record OrderItemRequest(
	Long bookId,
	Integer quantity,
	Long packagingOptionId,
	String itemCouponCode
) {
}
