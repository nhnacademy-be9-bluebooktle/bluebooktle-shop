package shop.bluebooktle.backend.book.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookCreateRequest {
	private String title;
	private String description;
	private LocalDate publishDate;
	private String isbn;

	private String author;
	private String publisher;
	private String categoryName;
	private String imageUrl;

	private BigDecimal price;
	private BigDecimal salePrice;
	private BigDecimal salePercentage; //할인율 어떻게 계산해야할지?

}
