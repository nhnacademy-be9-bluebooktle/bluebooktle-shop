package shop.bluebooktle.backend.book.dto.request.author;

import lombok.Builder;
import lombok.Value;

@Value // @AllArgsConstructor + @Getter
@Builder
public class AuthorRegisterRequest {
	String name;
}
