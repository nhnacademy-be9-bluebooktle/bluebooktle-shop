package shop.bluebooktle.backend.book.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookUpdateResponse {
	String title;
	String description;
}
