package shop.bluebooktle.backend.book.dto.request;

import jakarta.validation.constraints.NotNull;

public record BookInfoRequest(
	@NotNull
	Long bookId
) {
}
