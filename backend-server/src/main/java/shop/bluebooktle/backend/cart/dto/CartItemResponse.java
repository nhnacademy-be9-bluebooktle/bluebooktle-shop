package shop.bluebooktle.backend.cart.dto;

public record CartItemResponse(
	Long bookId,
	String title,
	String author,
	int quantity,
	int pricePerUnit,
	String thumbnailUrl
) {
	// public static CartItemResponse from(CartBook cartBook, Book book) {
	// 	return new CartItemResponse(
	// 		book.getId(),
	// 		book.getTitle(),
	// 		book.getAuthor(),
	// 		cartBook.getQuantity(),
	// 		book.getSalePrice(),
	// 		book.getThumbnailUrl()
	// 	);
	// }
}
