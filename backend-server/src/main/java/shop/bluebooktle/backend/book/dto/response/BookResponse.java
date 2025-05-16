package shop.bluebooktle.backend.book.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookResponse {
	String title;
	String description;
	LocalDate publishDate;
	String isbn;
}
