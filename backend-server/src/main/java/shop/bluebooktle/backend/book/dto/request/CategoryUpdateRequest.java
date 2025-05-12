package shop.bluebooktle.backend.book.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateRequest(
	@NotNull
	Long id,
	@NotBlank @Length(max = 50)
	String name
) {
}
