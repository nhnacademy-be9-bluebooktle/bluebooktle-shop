package shop.bluebooktle.backend.book.dto.response.author;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AuthorResponse {
	Long id;
	String name;
	String description;
	String authorKey;
	LocalDateTime createdAt;
}
