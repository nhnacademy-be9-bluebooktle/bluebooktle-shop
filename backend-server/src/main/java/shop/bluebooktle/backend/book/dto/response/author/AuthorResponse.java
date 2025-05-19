package shop.bluebooktle.backend.book.dto.response.author;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AuthorResponse {

	Long id;
	String name;
	LocalDateTime createdAt;
}
