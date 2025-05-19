package shop.bluebooktle.backend.book.dto.request.author;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AuthorUpdateRequest {

	@Length(max = 50)
	String name;

	String description;

}
