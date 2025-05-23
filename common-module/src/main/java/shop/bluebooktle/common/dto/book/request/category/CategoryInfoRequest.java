package shop.bluebooktle.common.dto.book.request.category;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CategoryInfoRequest(
	@Positive(message = "카테고리 ID는 1 이상의 값이어야 합니다.")
	@NotNull
	Long categoryId,
	@NotBlank
	@Length(max = 50)
	String name
) {
}
