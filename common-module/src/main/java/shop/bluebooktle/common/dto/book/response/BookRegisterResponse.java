package shop.bluebooktle.common.dto.book.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookRegisterResponse {

	String title;
	String description;
	String index;
	LocalDate publishDate;
	String isbn;
}
