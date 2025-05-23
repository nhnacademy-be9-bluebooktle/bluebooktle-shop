package shop.bluebooktle.backend.book.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CategoryRegisterRequest(
	@NotBlank @Length(max = 50)
	String name,
	Long parentCategoryId
) {
}
