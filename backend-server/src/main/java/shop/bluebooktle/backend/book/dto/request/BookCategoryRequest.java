package shop.bluebooktle.backend.book.dto.request;

public record BookCategoryRequest(
	Long bookId,
	Long categoryId) {
}
