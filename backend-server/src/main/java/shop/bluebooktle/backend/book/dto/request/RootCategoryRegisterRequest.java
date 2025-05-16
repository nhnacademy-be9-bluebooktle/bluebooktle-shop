package shop.bluebooktle.backend.book.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record RootCategoryRegisterRequest(
	@NotBlank @Length(max = 50)
	String rootCategoryName,
	@NotBlank @Length(max = 50)
	String childCategoryName
) {
}
