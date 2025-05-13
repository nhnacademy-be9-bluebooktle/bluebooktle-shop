package shop.bluebooktle.backend.book.dto.response.author;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthorResponse {
	Long id;
	String name;
	LocalDateTime createdAt;
}
