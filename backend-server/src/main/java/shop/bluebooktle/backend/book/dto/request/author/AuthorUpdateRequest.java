package shop.bluebooktle.backend.book.dto.request.author;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthorUpdateRequest {
	String name;
}
