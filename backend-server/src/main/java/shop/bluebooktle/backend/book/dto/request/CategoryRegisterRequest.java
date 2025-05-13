package shop.bluebooktle.backend.book.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CategoryRegisterRequest(
	@NotBlank @Length(max = 50)
	String name,
	@Positive(message = "상위 카테고리의 ID는 1 이상의 양수여야 합니다.")
	Long parentCategoryId
) {
}
