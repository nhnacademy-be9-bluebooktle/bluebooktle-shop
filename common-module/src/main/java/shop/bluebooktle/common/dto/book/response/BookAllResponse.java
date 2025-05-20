package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;

@Getter
@Builder
@Value
@AllArgsConstructor
public class BookAllResponse {
	Long id;
	String title;
	String description;
	LocalDateTime publishDate;
	String isbn;
	BigDecimal price;
	BigDecimal salePrice;
	Integer stock;
	BigDecimal salePercentage;
	String thumbnailUrl;
	List<String> authors;
	String publisher;
	List<String> categories;
	List<String> tags;
	BookSaleInfo.State state;
	Long viewCount;
	Long searchCount;
}
