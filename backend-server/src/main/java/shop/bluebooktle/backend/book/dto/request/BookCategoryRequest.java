package shop.bluebooktle.backend.book.dto.request;

import jakarta.validation.constraints.NotNull;

public record BookCategoryRequest(
	@NotNull
	Long bookId,
	@NotNull
	Long categoryId) {
}
