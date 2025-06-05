package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class BookLikesListResponse {
	Long bookId;
	String bookName;
	List<String> authorName;
	String imgUrl;
	BigDecimal price;
}
