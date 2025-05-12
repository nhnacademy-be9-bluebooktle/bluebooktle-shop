package shop.bluebooktle.backend.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;

@Getter
@Value
@AllArgsConstructor
@Builder
public class BookRegisterByAladinRequest {
	String isbn;
	Integer stock;
	Boolean isPackable;
	BookSaleInfo.State state;
}
