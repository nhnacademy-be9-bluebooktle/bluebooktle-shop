package shop.bluebooktle.common.dto.book.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookSummaryResponseDto {
	private Long id;
	private String title;
	private String authorName;
	private String publisherName;
	private String imgUrl;
}
