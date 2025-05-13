package shop.bluebooktle.backend.book.dto.request.author;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
public class AuthorUpdateRequest {
	String name;
}
