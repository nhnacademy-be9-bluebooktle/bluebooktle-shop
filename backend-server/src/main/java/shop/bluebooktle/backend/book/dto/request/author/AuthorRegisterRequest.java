package shop.bluebooktle.backend.book.dto.request.author;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value // @AllArgsConstructor + @Getter
@Builder
public class AuthorRegisterRequest {
	String name;
}
