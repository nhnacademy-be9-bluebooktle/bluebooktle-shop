package shop.bluebooktle.common.dto.book.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;

@Getter
@Builder
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class BookUpdateFormRequest {
	Long id;
	String title;
	String description;
	LocalDateTime publishDate;
	String index;
	String isbn;
	BigDecimal price;
	BigDecimal salePrice;
	Integer stock;
	BigDecimal salePercentage;
	String imgUrl;
	Boolean isPackable;
	List<AuthorResponse> authors;
	List<PublisherInfoResponse> publishers;
	List<CategoryResponse> categories;
	List<TagInfoResponse> tags;
	BookSaleInfoState state;
	Long viewCount;
	Long searchCount;
	Long reviewCount;
	BigDecimal star;
	LocalDateTime createdAt;
}
