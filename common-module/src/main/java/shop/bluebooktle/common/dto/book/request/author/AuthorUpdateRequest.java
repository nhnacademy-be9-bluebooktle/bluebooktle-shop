package shop.bluebooktle.common.dto.book.request.author;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AuthorUpdateRequest {

	@NotBlank
	@Length(max = 50)
	String name;

}
