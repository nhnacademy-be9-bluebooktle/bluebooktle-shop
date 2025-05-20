package shop.bluebooktle.common.dto.book.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookInfoRequest(
	@Positive(message = "도서 ID는 1 이상의 값이어야 합니다.")
	@NotNull
	Long bookId
) {
}
