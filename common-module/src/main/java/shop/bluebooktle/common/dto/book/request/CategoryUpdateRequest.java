package shop.bluebooktle.common.dto.book.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
	@NotBlank
	@Length(max = 50)
	String name
) {
}
