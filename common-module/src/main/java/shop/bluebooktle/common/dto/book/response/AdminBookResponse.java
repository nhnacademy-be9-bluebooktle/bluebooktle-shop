package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminBookResponse {
	Long bookId;
	String isbn;
	String title;
	List<String> authors;
	List<String> publishers;
	BookSaleInfoState bookSaleInfoState;
	BigDecimal salePrice;
	Integer stock;
	LocalDateTime publishDate;
}
