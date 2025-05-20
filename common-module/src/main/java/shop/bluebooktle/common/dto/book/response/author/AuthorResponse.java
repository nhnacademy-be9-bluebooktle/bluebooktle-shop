package shop.bluebooktle.common.dto.book.response.author;

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
	LocalDateTime createdAt;
}
